package base.disk;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXParseException;

public class DiskConfFile {
	
	/** The instance. */
	static DiskConfFile instance ;
	
	/** Disks path */
	private String[] disks;
	
	/** The nodes offset on the disk */
    private long[] nOffsets;
    /** The new node offset */
    private long[] newNodeOffsets;
	
	/** If true all the data will be encrypted */
	private boolean encrypted;
	
    /** The boot sector offset on the disk */
    private long sbOffset;
    /** The boot sector size on the disk */
    private long sbSize;    
    /** The cache offset on the disk */
    private long cOffset;
    /** The cache size on the disk */
    private long cSize; 
    /** Number of element for every cache level */
    private long numLvlEle;    
    
    /** Key domain start */
    private long keyDomainStart;
    /** Key domain end */
    private long keyDomainEnd;
    /** The root pid */
    private long rootPid;
    
    /** Number of keys in the bptree */
    private int keyNum;
    /** The bptree height */
    private int height;
    /** The maximum number of keys in the leaf node, M must be > 0 */
    private int M;
    /** The maximum number of keys in inner node, the number of pointer is N+1, N must be > 2 */
    private int N;
    /** The minimum number of keys in the root node, the number of pointer is minnumkeys+1 */
    private int minnumkeys;
	
    /**
	 * The class constructor that would be call just once
	 * 
	 * @param conf_file the path of the configuration file
	 */
	private DiskConfFile(String conf_file) {
		
		try {
	    	DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	        Document doc = docBuilder.parse (new File(conf_file));
            
	        NamedNodeMap nnmAttributes;
	        
	        
	        
	        nnmAttributes = doc.getElementsByTagName("filesystem").item(0).getAttributes();	
			encrypted	= Boolean.parseBoolean(nnmAttributes.getNamedItem("encrypted").getNodeValue());
			
			disks = new String[4];
			
			nnmAttributes = doc.getElementsByTagName("disk1").item(0).getAttributes();			
			disks[0]		= nnmAttributes.getNamedItem("path").getNodeValue();
			nnmAttributes = doc.getElementsByTagName("disk2").item(0).getAttributes();			
			disks[1]		= nnmAttributes.getNamedItem("path").getNodeValue();
			nnmAttributes = doc.getElementsByTagName("disk3").item(0).getAttributes();			
			disks[2]		= nnmAttributes.getNamedItem("path").getNodeValue();
			nnmAttributes = doc.getElementsByTagName("disk4").item(0).getAttributes();			
			disks[3]		= nnmAttributes.getNamedItem("path").getNodeValue();
			
			nnmAttributes = doc.getElementsByTagName("superblock").item(0).getAttributes();				
			sbOffset	= Long.valueOf(nnmAttributes.getNamedItem("offset").getNodeValue());
			sbSize	= Long.valueOf(nnmAttributes.getNamedItem("size").getNodeValue());
			
			nnmAttributes = doc.getElementsByTagName("cachesector").item(0).getAttributes();				
			cOffset	= Long.valueOf(nnmAttributes.getNamedItem("offset").getNodeValue());
			cSize	= Long.valueOf(nnmAttributes.getNamedItem("size").getNodeValue());
			numLvlEle = Long.valueOf(nnmAttributes.getNamedItem("numlvlele").getNodeValue());			
			
			nOffsets = new long[4];
			newNodeOffsets = new long[4];
			for( int i = 0 ; i < 4 ; i++ ) {
				nnmAttributes = doc.getElementsByTagName("nodessector").item(i).getAttributes();				
				nOffsets[i]	= Long.valueOf(nnmAttributes.getNamedItem("offset").getNodeValue());
				newNodeOffsets[i] = Long.valueOf(nnmAttributes.getNamedItem("newnodeoffset").getNodeValue());
			}
			
			nnmAttributes = doc.getElementsByTagName("key").item(0).getAttributes();				
			keyNum	= Integer.valueOf(nnmAttributes.getNamedItem("num").getNodeValue());
			keyDomainStart	= Long.valueOf(nnmAttributes.getNamedItem("domainstart").getNodeValue());
			keyDomainEnd	= Long.valueOf(nnmAttributes.getNamedItem("domainend").getNodeValue());
			
			
			nnmAttributes = doc.getElementsByTagName("bptree").item(0).getAttributes();				
			rootPid	= Long.valueOf(nnmAttributes.getNamedItem("rootpid").getNodeValue());
			height	= Integer.valueOf(nnmAttributes.getNamedItem("height").getNodeValue());
			N	= Integer.valueOf(nnmAttributes.getNamedItem("innernodedegree").getNodeValue());
			M	= Integer.valueOf(nnmAttributes.getNamedItem("leafnodedegree").getNodeValue());
			minnumkeys = Integer.valueOf(nnmAttributes.getNamedItem("minnumkeys").getNodeValue());
	        
		}catch (java.io.FileNotFoundException fnfe) {
			fnfe.printStackTrace();
        }catch (SAXParseException saxpe) {
        	saxpe.printStackTrace();
        }catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	/**
	 * Gets the single instance of Xml.
	 * 
	 * @return single instance of Xml
	 */
	public static DiskConfFile getInstance(String conf_file) {
		
		if (instance == null)
			instance = new DiskConfFile(conf_file) ;

		return instance ;
	}

	/**
	 * Gets the disks path. 
	 * 
	 * @return the disks path
	 */
	public String[] getDisks() {
		return disks;
	}
	
	
	/**
	 * Checks if is encrypted.
	 * 
	 * @return true, if is encrypted
	 */
	public boolean isEncrypted() {
		return encrypted;
	}

	/**
	 * Gets the superblock offset.
	 * 
	 * @return the superblock offset
	 */
	public long getSbOffset() {
		return sbOffset;
	}
	
	/**
	 * Gets the superblock size.
	 * 
	 * @return the superblock size
	 */
	public long getSbSize() {
		return sbSize;
	}

	/**
	 * Gets the cache offset.
	 * 
	 * @return the cache offset
	 */
	public long getCOffset() {
		return cOffset;
	}
	
	/**
	 * Gets the number of element for cache level.
	 * 
	 * @return the number of element for cache level
	 */
	public long getNumLvlEle() {
		return numLvlEle;
	}

	/**
	 * Gets the cache size.
	 * 
	 * @return the cache size
	 */
	public long getCSize() {
		return cSize;
	}

	
	/**
	 * Gets the nodes block offset of the disks.
	 * 
	 * @return the nodes block offset of the disks
	 */
	public long[] getNOffsets() {
		return nOffsets;
	}

	/**
	 * Gets the new node offset of the disks.
	 * 
	 * @return the new node offset of the disks
	 */
	public long[] getNewNodeOffsets() {
		return newNodeOffsets;
	}

	/**
	 * Gets the b+tree height.
	 * 
	 * @return the b+tree height
	 */
	public long getHeight() {
		return height;
	}

	/**
	 * Gets the key domain start.
	 * 
	 * @return the key domain start
	 */
	public long getKeyDomainStart() {
		return keyDomainStart;
	}

	/**
	 * Gets the key domain end.
	 * 
	 * @return the key domain end
	 */
	public long getKeyDomainEnd() {
		return keyDomainEnd;
	}
	
	/**
	 * Gets the minnumkeys in the root node
	 * The root node will include a number of keys greater or equal to minnumkeys
	 * @return the min num keys in the root node
	 */
	public int getMinNumKeys() {
		return minnumkeys;
	}

	/**
	 * Gets the root pid.
	 * 
	 * @return the root pid
	 */
	public long getRootPid() {
		return rootPid;
	}

	/**
	 * Gets the key num.
	 * 
	 * @return the key num
	 */
	public int getKeyNum() {
		return keyNum;
	}

	/**
	 * Gets the leaf node number of key.
	 * 
	 * @return the leaf node number of key.
	 */
	public int getM() {
		return M;
	}

	/**
	 * Gets the inner node number of key.
	 * 
	 * @return the inner node number of key
	 */
	public int getN() {
		return N;
	}
	
}
