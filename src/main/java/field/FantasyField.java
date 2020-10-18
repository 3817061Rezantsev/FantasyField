package field;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class FantasyField {
	private ArrayList<String> bank = new ArrayList<>();
	private String word;
	private String name;
	private String oldData;
	private String newData;
	private HashMap<String, Integer> data = new HashMap<>();
	private char[] field;
	private boolean guessed = false;
	int trial = 0;

	public FantasyField() throws IOException {
		FileReader fr = new FileReader("bank.txt");
		Scanner scan = new Scanner(fr);
		while (scan.hasNextLine()) {
			bank.add(scan.nextLine());
		}
		fr.close();
		scan.close();
	}

	public void getPlayer(String player) throws IOException {
		FileReader fr = new FileReader("data.txt");
		Scanner scan = new Scanner(fr);
		boolean flag = true;
		while (scan.hasNextLine()) {
			String line = scan.nextLine();
			if (player.equals(line)) {
				flag = false;
				line = scan.nextLine();
				int index = line.indexOf(':');
				while (index != -1) {
					String key = line.substring(0, index);
					int value = Integer.valueOf(line.substring(index + 1, line.length()));
					data.put(key, value);
					if (scan.hasNextLine()) {
						line = scan.nextLine();
					} else {
						break;
					}
					index = line.indexOf(':');
				}
				oldData = setTextData();
				if (index == -1) {
					break;
				}

			}
		}
		fr.close();
		scan.close();
		if (flag) {
			setNewPlayer(player);
		}
	}

	public void setNewPlayer(String player) throws IOException {
		Charset charset = StandardCharsets.UTF_8;
        Path path = Paths.get("data.txt");
        oldData = new String(Files.readAllBytes(path), charset);
		FileWriter nFile = new FileWriter("data.txt");
		nFile.write(oldData);
		nFile.write(player + "\n");
		for (String str : bank) {
			nFile.write(str + ":0\n");
			data.put(str, 0);
		}
		oldData = setTextData();
		nFile.close();
	}

	public String setTextData() {
		String currData = "";
		currData += name + "\n";
		for (String str : bank) {
			currData += str + ":" + data.get(str) + "\n";
		}
		return currData;
	}

	public void setBank(String[] bank) {
		for (String str : bank) {
			this.bank.add(str);
		}
	}

	public void setWord() {
		int i = (int) (Math.random() * bank.size());
		word = bank.get(i);
		field = new char[word.length()];
		for (int j = 0; j < word.length(); j++) {
			field[j] = '_';
		}
	}

	public void guess(String s) throws IOException {
		if (s.length() == 1) {
			int i = word.indexOf(s);
			if (i == -1) {
				System.out.println("There is no such letter");
			}
			char[] temp = new char[word.length()];
			for (int j = 0; j < word.length(); j++) {
				temp[j] = word.charAt(j);
			}
			while (i != -1) {
				field[i] = temp[i];
				int k = i;
				temp[i] = '_';
				for (int j = 0; j < word.length(); j++) {
					if (temp[j] == s.charAt(0)) {
						i = j;
					}
				}
				if (k == i) {
					i = -1;
				}
			}
			trial++;
			String str = new String(field);
			if (str.indexOf("_") != -1) {
				System.out.println("Trial " + trial + ": " + str);
			} else {
				guessed = true;
				if (data.get(word) == 0 || data.get(word) > trial) {
					data.replace(word, trial);
					newData = setTextData();
					Charset charset = StandardCharsets.UTF_8;
			        Path path = Paths.get("data.txt");
			        Files.write(path,
			            new String(Files.readAllBytes(path), charset).replace(oldData, newData)
			                .getBytes(charset));
				}
				System.out.println("Congratulation!");
				System.out.println("You got in " + trial + " trials!");
			}

		} else if (s.length() == word.length() && s.equals(word)) {
			guessed = true;
			trial++;
			if (data.get(word) == 0 || data.get(word) > trial) {
				data.replace(word, trial);
				newData = setTextData();
				Charset charset = StandardCharsets.UTF_8;
		        Path path = Paths.get("data.txt");
		        Files.write(path,
		            new String(Files.readAllBytes(path), charset).replace(oldData, newData)
		                .getBytes(charset));
			}
			System.out.println("Congratulation!");
			System.out.println("You got in " + trial + " trials!");
		} else {
			guessed = true;
			System.out.println("You didn't guess the word");
		}
	}

	public void startGame() {
		setWord();
		Scanner in = new Scanner(System.in);
		System.out.println("Enter your name");
		name = in.nextLine();
		try {
			getPlayer(name);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (!guessed) {
			System.out.println("Key in one character or your guess word: ");
			try {
				guess(in.nextLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		in.close();
	}

	public static void main(String args[]) {
		FantasyField f;
		try {
			f = new FantasyField();
			f.startGame();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// String[] str = { "testing", "data", "main" };
		// f.setBank(str);

	}
}
