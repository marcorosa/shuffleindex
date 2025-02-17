package server.pir;

import base.bptree.Bptree;
import base.disk.Disk;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;

import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.csv.*;

/**
 * The class Database
 * 
 * @author Tommaso
 *
 */
public class Database {

	/**
	 * Creates the b+tree and saves it on the disk, all the informations used
	 * during the creation are stored in the file disk_conf.xml
	 */
	public static void create() throws FileNotFoundException, IOException {

		Disk<Long, String> disk = new Disk<Long, String>(true);
		Bptree<Long, String> bptree = new Bptree<Long, String>(disk);

		//Open CSV file containing data
		File csv = new File("pa.csv");
		CSVParser parser = CSVParser.parse(csv, Charset.forName("UTF-16"),CSVFormat.DEFAULT.withDelimiter(';'));

		List<String> db = new ArrayList<String>();
		for (CSVRecord record : parser) {
			db.add(record.get(0));
		}


		/*
		* convertByte - contains the string converted to UTF-16 (convert to bytes->convert to string)
		* str - variable for padding
		* i - iterator for number of keys
		* */

		byte[] convertByte = null;
		String str;
		long i;

		for (i = 0; i < disk.getDiskSuperBlock().getKeyNum() && i < db.size(); i++) {
			/*
			* Pad the string to 100 characters in order to have the same length
			* Convert to bytes to ensure UTF-16 encoding
			* */
			str = String.format("%-100s", db.get((int) i));
			convertByte = str.getBytes();
			bptree.insert(i + 1, new String(convertByte));
		}

		System.out.println("Disk created");
		parser.close();
		bptree.close();
		disk.close();

	}

}