package base.disk;

import java.io.EOFException;
import java.io.File;
import java.io.Serializable;

import base.VarUtils;
import base.bptree.INode;
import base.bptree.LNode;
import base.bptree.Node;
import base.crypto.Crypto;

/**
 * The disk class
 * @author Tommaso
 *
 * @param <Key> the bptree key type
 * @param <Value> the bptree value type
 */
public class Disk<Key extends Comparable<? super Key>, Value> extends DiskIO implements Serializable{

	/** Buffer */
	private Buffer<Key, Value> buffer;
	/** The superblock */
    private DiskSuperBlock superBlock;
    /** The cacheblock */
    private DiskCacheBlock cacheBlock;
       
    /**
     * The class constructor
     * 
     * @param create if true the existing disk would be deleted and a new one created otherwise the existing one would be  loaded
     */
    public Disk(boolean create) {   	
    		
    	super(DiskConfFile.getInstance(CONF_FILE).getDisks());
    	
    	if(create) {
    	
    		closeDisks();
    		
    		File[] files = new File[4];
    		
    		for( int i=0 ; i<4 ; i++ ) {
    			files[i] = new File(disks[i]);	
    			if( files[i].exists() ) {
    				files[i].delete();
        		}
    		}
    		  		
    		openDisk();
    		
    		superBlock = new DiskSuperBlock ( disks, CONF_FILE ); 
    		superBlock.save(); 
    		
    		cacheBlock = new DiskCacheBlock( disks, superBlock );
    		cacheBlock.init();
    		
		} else {			
			
			superBlock = new DiskSuperBlock( 	disks , 
												DiskConfFile.getInstance(CONF_FILE).getSbOffset(),
												(int)DiskConfFile.getInstance(CONF_FILE).getSbSize()
											);	
			
			cacheBlock = new DiskCacheBlock( disks, superBlock );
			cacheBlock.load();
			
		}
    	
    	buffer = new Buffer<Key, Value>( this, (int)superBlock.getHeight() );
    		
	}
	
    
    public void registerWriteAccess(long offset) {  // se dio vuole si usa un solo disco sul server!!!
    	if (superBlock.getNewNodeOffsets(0) < offset) {
    		superBlock.setNewNodeOffset(0,offset);
    	}
    }
    /**
     * Writes a node to the first disk if it isn't in the buffer
     * 
     * @param node 
     * @param create if true the node is new and must be append to the other nodes on the disk otherwise the existing one would be overwritten
     */
    public void  writeNode(Node<Key, Value> node, boolean create) {
    	
    	if(create) {
    		
    		writeNode(node, create, 0);
    		
    	} else {
    		int disk = (int)( (node.pid & 0x6000000000000000L) >> 61 );
    		//int disk = (int) (node.pid / 100000000);
			writeNode(node, create, disk);
    		
    	}
    	
    }
    
    /**
     * Writes a node to the specified disk if it isn't in the buffer
     * 
     * @param node 
     * @param create if true the node is new and must be append to the other nodes on the disk otherwise the existing one would be overwritten
     * @param i the disk number
     */
    public void  writeNode(Node<Key, Value> node, boolean create, int i) {
    	
    	if(! buffer.isInBuffer(node.pid)) {	  
    		
	    	if(create) {
	    		
	    		node.pid = superBlock.getNewNodeOffsets(i);
	    		node.vid = node.pid;
	    		
	    		if(node instanceof INode<?,?>) {	
	    			superBlock.addInnerNode();
	    		} else {
	    			superBlock.addLeafNode();
	    		}
	    		
	    		//Adds pid prefix
	    		node.pid = node.pid  | ( (long)i << 61 ) ;
	    		
	    	}	
	    	
	    	//Removes prefix
	    	long offset = node.pid & 0x1FFFFFFFFFFFFFFFL;
	    	
	    	byte[] nodebytes = node.getBytes();
	    	
	    	if(DiskConfFile.getInstance(CONF_FILE).isEncrypted()) {
	    		
	    		byte[] toEncrypt = new byte[nodebytes.length -1];
	    		System.arraycopy(nodebytes, 1, toEncrypt, 0, nodebytes.length-1);
	    		byte[] encrypted = Crypto.encryptBytes(Crypto.loadKey(), toEncrypt);
	    		
	    		byte[] toStore = new byte[encrypted.length+1];
	    		toStore[0] = nodebytes[0];
	    		System.arraycopy(encrypted,0,toStore,1,encrypted.length);
	    		
	    		nodebytes = toStore;
	    	}
	    	
	    	//writeBytes(node, i);

			writeBytes(nodebytes, offset, i);

			if(create)
				superBlock.setNewNodeOffset(i, offset + (long) nodebytes.length);
		}
    	
    }
 
    /** 
     * Reads all the bytes containing the node information
     * 
     * @param pid of the node
     * 
     * @return the node's bytes
     */
    public byte[] readNodeBytes(long pid) {
    	
    	int length;
    	byte[] nodeBytes = null;
    	
    	long offset = pid & 0x1FFFFFFFFFFFFFFFL;
    	int disk = (int)( (pid & 0x6000000000000000L) >> 61 );
		//int disk = (int) (pid / 100000000);
    	
    	try {
    		
	    	rafs[disk].seek(offset);
	    	byte info = rafs[disk].readByte();
	    	
	    	rafs[disk].seek(offset);
	    	//Length read must be modified according to the fixed length of strings
	    	if( (info & (byte)128) != 0 ) {
	    		length = (2*superBlock.getN()+4)*8+4;
	    	} else {	    		    		
	    		length = (4 + (superBlock.getM() + 3) * 8 + superBlock.getM()* VarUtils.padlen);
	    	}
	    	
	    	if(DiskConfFile.getInstance(CONF_FILE).isEncrypted()) {
	    		length = length + 16 - (length % 16);
	    	}
	    	
	    	nodeBytes = new byte[length + 1 ];
    		rafs[disk].read(nodeBytes, 0, ( length + 1 ));
	    	
    	}  catch(EOFException e) {
    		System.out.println("Disk file not found - An empty disk file will now be created");
    	}   catch(Exception e2){
			e2.printStackTrace();
		}
    	
    	return nodeBytes;
    	
    }
    
    /**
     * Reads a node from the disk and place it into the buffer
     * 
     * @param pid of the node
     * 
     * @return the requested node
     */

	//Read a single node, lenght must be modified according to the length of keys and values
    public Node<Key, Value> readNode(long pid) {
    	
    	int length;
    	Node<Key, Value> node = null;
    	
    	long offset = pid & 0x1FFFFFFFFFFFFFFFL;
    	int disk = (int)( (pid & 0x6000000000000000L) >> 61 );
		//int disk = (int) (pid / 100000000);


    	node = buffer.get(pid);
    	
    	if(node != null)
    		return node;
    	    	
    	byte[] nodeBytes = null;
    	
    	try {
    		
	    	rafs[disk].seek(offset);
			byte info = rafs[disk].readByte();

			if( (info & (byte)128) != 0 ){

	    		length = (2*superBlock.getN()+4)*8+4;
	    		
	    		if(DiskConfFile.getInstance(CONF_FILE).isEncrypted()){
	    			length = length + 16 - (length % 16);
	    		}
	    		
	    		nodeBytes = new byte[length];
	    		rafs[disk].read(nodeBytes, 0, length);
	    		
	    		if(DiskConfFile.getInstance(CONF_FILE).isEncrypted()){
	    			nodeBytes = Crypto.decryptBytes(Crypto.loadKey(), nodeBytes);
	    		}
	    		
	    		node = new INode<Key, Value>(this, superBlock.getN(), nodeBytes);
	    			    		
	    	} else {
				//Modified to match the length of values
	    		length = (superBlock.getM()+3)*8+4+superBlock.getM()*VarUtils.padlen;
	    		
	    		if(DiskConfFile.getInstance(CONF_FILE).isEncrypted()){
	    			length = length + 16 - (length % 16);
	    		}
	    		
	    		nodeBytes = new byte[length];
	    		rafs[disk].read(nodeBytes, 0, length);
	    		
	    		if(DiskConfFile.getInstance(CONF_FILE).isEncrypted()){
	    			nodeBytes = Crypto.decryptBytes(Crypto.loadKey(), nodeBytes);
	    		}
	    		
	    		node = new LNode<Key, Value>(this, superBlock.getM(), superBlock.getN(), nodeBytes);
	    		
	    	}
	    	
	    	
    	}  catch(Exception e) {
    		e.printStackTrace();
    	}   	
    	
    	buffer.put(node);
    	
    	return node;
    	
    }
    
    /**
     * Gets the disk's super block
     * 
     * @return the disk's super block
     */
	public DiskSuperBlock getDiskSuperBlock() {
		
		return superBlock;
		
	}
	
	/**
     * Gets the disk's cache block
     * 
     * @return the disk's cache block
     */
	public DiskCacheBlock getDiskCacheBlock() {
		
		return cacheBlock;
		
	}

	/**
	 * 
	 * Empties the buffer and save the superblock on the disk
	 * 
	 */
	public void close() {
		
		buffer.emptyBuffer();
		superBlock.save();
		closeDisks();
		
	}
	
	public void updateSuperBlock(long pd, long off0, long off1, long off2, long off3, int keynum){
			superBlock.setHeight(pd);
			superBlock.setNewNodeOffset(0, off0);
			superBlock.setNewNodeOffset(1, off1);
			superBlock.setNewNodeOffset(2, off2);
			superBlock.setNewNodeOffset(3, off3);
			superBlock.setKeyNumber(keynum);
			superBlock.save();
	} 


}
