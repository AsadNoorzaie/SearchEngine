package lse;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		
		// dont forget to throw an error when no file found
		if (docFile == null)
			throw new FileNotFoundException();
		
		HashMap<String, Occurrence> loadkeywords = new HashMap<String, Occurrence>();
		Scanner sc = new Scanner(new File(docFile));
		while (sc.hasNext()) {
			String keyword = sc.next();
			keyword = getKeyword(keyword);
			
			if(keyword==null)
				continue;
			//check to see if keyword already exists in hashMap
			if(loadkeywords.get(keyword)!=null) {
				// update the frequency
				Occurrence update =loadkeywords.get(keyword);
				update.frequency++;
				loadkeywords.put(keyword, update);
				continue;
			}
			//else key does not exists so add
			else {
				Occurrence update = new Occurrence(docFile, 1);
				loadkeywords.put(keyword, update);
			}
		}
		
		sc.close();
		
		System.out.println("This is loading into a hashtable for: "+docFile);
		for(String name: loadkeywords.keySet()) {
			
			String key = name.toString();
			String value = loadkeywords.get(name).toString();
			System.out.println(key+": " + value);
		}	
		return loadkeywords;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		
		for(String key: kws.keySet()) {
			//key already exists in master hash table
			if(keywordsIndex.containsKey(key)) {
				ArrayList<Occurrence> newlist = keywordsIndex.get(key);
				newlist.add(kws.get(key));
				insertLastOccurrence(newlist);
				keywordsIndex.put(key, newlist);
			}
			//key does not exist in master hash table, put key value into arraylist of type occurrence and add into master hastable
			else {
				ArrayList<Occurrence> newvalue = new ArrayList<Occurrence>();
				Occurrence valueofKey = kws.get(key);
				newvalue.add(valueofKey);
				insertLastOccurrence(newvalue);
				keywordsIndex.put(key, newvalue);
			}
		}
		
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		
		word = word.toLowerCase();
		while(true) {
			if(word.charAt(word.length()-1) == '?' || word.charAt(word.length()-1) == ':' || word.charAt(word.length()-1) == ';' ||word.charAt(word.length()-1) == '!' 
					|| word.charAt(word.length()-1) == ',' || word.charAt(word.length()-1) == '.') {
				word = word.substring(0, word.length()-1);
				continue;
			}
			if(noiseWords.contains(word))
				return null;
			if(Character.isLetter(word.charAt(word.length()-1))){
				boolean check = Alpha(word);
				if(check)
					return word;
				else
					return null;
				
			}
					
		}
	}
	private boolean Alpha(String name) {
	    return name.matches("[a-zA-Z]+");
	}

	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		
		if(occs.size() == 1)
			return null;
		ArrayList<Integer> middleIndexes = new ArrayList<>();
		Occurrence temp = occs.get(occs.size()-1);
		int target = temp.frequency;
		System.out.println();
		System.out.println("Printing out target: "+target);
		occs.remove(occs.size()-1);
		int low = 0;
		int high = occs.size()-1;
		int middle = 0;
		int frequency = 0;
		int standard = 0;
		while(low<=high) {
			if(low==high) {
				// check where to insert target
				if(occs.get(low).frequency > target) {
					// put target occurrence into next index
					occs.add(low+1, temp);
					middleIndexes.add(low+1);
					System.out.println("Printing out middle indexes");
					System.out.println(middleIndexes);
					return middleIndexes;
				}
				if(occs.get(low).frequency < target) {
					//put target at current index
					occs.add(low, temp);
					middleIndexes.add(low);
					System.out.println("Printing out middle indexes");
					System.out.println(middleIndexes);
					return middleIndexes;
				}
				if(occs.get(low).frequency == target) {
					standard = low;
				}
			}
			middle = (low+high)/2;
			frequency = occs.get(middle).frequency;
			if(frequency < target) {
				high = middle-1;
				middleIndexes.add(middle);
				continue;
			}
			if(frequency > target) {
				low = middle+1;
				middleIndexes.add(middle);
				continue;
			}
			if(frequency == target) {
				// ask sesh about this as well
				standard = middle;
				middleIndexes.add(middle);
				break;
			}
			
		}
		if(high<low) {
			// 2 zeros are printed for middleIndexes, that right?
			middleIndexes.add(low);
			occs.add(low, temp);
			System.out.println("Printing out middle indexes");
			System.out.println(middleIndexes);
			return middleIndexes;
		}
		if(frequency == target) {
			// check below if correct
			occs.add(standard+1, temp);
		}
		
		System.out.println("Printing out middle indexes");
		System.out.println();
		System.out.println(middleIndexes);
		return middleIndexes;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		if (docsFile == null)
			throw new FileNotFoundException();
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}

		System.out.println();
		System.out.println("This is printing masterhashtable");

		for(String name: keywordsIndex.keySet()) {
			String key = name.toString();
			String value = keywordsIndex.get(name).toString();
			System.out.println(key+": " + value);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
			kw1 = kw1.toLowerCase();
			kw2 = kw2.toLowerCase();
			ArrayList<Occurrence> word2 = new ArrayList<Occurrence>(); 
			ArrayList<Occurrence> word1 = new ArrayList<Occurrence>(); 
			ArrayList<String> searchResult = new ArrayList<String>(); 
			if (keywordsIndex.containsKey(kw1)) {
				word1 = keywordsIndex.get(kw1);
			}
			if (keywordsIndex.containsKey(kw2)) {
				word2 = keywordsIndex.get(kw2);
			}
			int wordInt = 0; 
			int word2Int = 0; 
			int search = 0; 
			while((search <= 5) && (wordInt < word1.size() && word2Int < word2.size())) {
					if ((word1.get(wordInt).frequency > word2.get(word2Int).frequency)) {
						if (!searchResult.contains(word1.get(wordInt).document)) {
							searchResult.add(word1.get(wordInt).document); 
							search++; 
						}
						wordInt++; 
					} else if ((word1.get(wordInt).frequency == word2.get(word2Int).frequency)) {
					
						if (!searchResult.contains(word1.get(wordInt).document)) {
							searchResult.add(word1.get(wordInt).document); 
							search++; 
						}
						wordInt++;  
					} else  {
						if (!searchResult.contains(word2.get(word2Int).document)) {
							searchResult.add(word2.get(word2Int).document); 
							
							search++; 
						}
						word2Int++; 
					}
			}
			while (search<= 5 && wordInt < word1.size()) {
				if (!searchResult.contains(word1.get(wordInt).document)) {
					searchResult.add(word1.get(wordInt).document); 	
					search++; 
				}
				wordInt++;  
			}
			while (word2Int < word2.size() && search<= 5) {
				if (!searchResult.contains(word2.get(word2Int).document)) {
					searchResult.add(word2.get(word2Int).document); 
					search++; 
				}
				word2Int++;  
			}

		System.out.println();
		System.out.println("Printing out top 5 for keywords: "+kw1+" & "+kw2);
		System.out.println(searchResult);
		System.out.println();
		if (searchResult.size() > 0) {
			return searchResult;
		} else {
			return null; 
		}
	}
}