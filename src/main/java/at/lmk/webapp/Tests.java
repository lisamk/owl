package at.lmk.webapp;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Tests implements Tags {

	public static void main(String[] args) throws IOException {
		File f = new File("./src/main/resources/hibernate.cfg.xml");
		Scanner myReader = new Scanner(f);
		while (myReader.hasNextLine()) {
			String data = myReader.nextLine();
			System.out.println(data);
		}
		myReader.close();
	}

}
