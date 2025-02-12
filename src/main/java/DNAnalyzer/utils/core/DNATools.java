/*
 * Copyright © 2025 Piyush Acharya. Some rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * You are entirely responsible for the use of this application, including any and all activities that occur.
 * While DNAnalyzer strives to fix all major bugs that may be either reported by a user or discovered while debugging,
 * they will not be held liable for any loss that the user may incur as a result of using this application, under any circumstances.
 *
 * For further inquiries, please contact reach out to contact@dnanalyzer.live
 */

package DNAnalyzer.utils.core;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Functionality for dna sequence manipulation.
 *
 * @param dna the dna sequence
 */
public record DNATools(String dna) {

  private static final char[] BASES = { 'A', 'T', 'C', 'G' };
  private static Random random = new Random();

  /**
   * Checks if the DNA sequence is valid.
   *
   * @throws IllegalArgumentException if the DNA sequence contains invalid characters.
   */
  public void isValid() {
    if (!dna.matches("[atgcn]+")) {
      throw new IllegalArgumentException("Invalid characters present in DNA sequence.");
    }
  }

  /**
   * Replaces the input string with the provided replacement string.
   *
   * @param input is the original string to be replaced
   * @param replacement is the string that will replace the input string
   */
  public DNATools replace(final String input, final String replacement) {
    return new DNATools(this.dna.replace(input, replacement));
  }

  /** Reverse the DNA sequence. */
  public DNATools reverse() {
    return new DNATools(new StringBuilder(dna).reverse().toString());
  }

  /** Get the DNA sequence. */
  public String getDna() {
    return dna;
  }

  /** Get the reverse compliment of a DNA sequence. */
  public String getReverseComplement() {
    return new StringBuilder(dna)
        .reverse()
        .toString()
        .replace("a", "T")
        .replace("t", "A")
        .replace("g", "C")
        .replace("c", "G")
        .toLowerCase();
  }

  /**
   * Mutates the DNA sequence by a specified number of random base mutations.
   *
   * @param numMutations the number of mutations to apply
   * @return a new mutated DNATools instance with the mutated DNA
   */
  public DNATools mutate(int numMutations) {
    StringBuilder mutatedDna = new StringBuilder(dna);

    if (numMutations > mutatedDna.length()) {
      System.out.println("Warning: Number of requested mutations exceeds DNA length. Limiting to " + mutatedDna.length());
      numMutations = mutatedDna.length();
    }

    // Create a list of all possible positions
    List<Integer> availablePositions = new ArrayList<>();
    for (int i = 0; i < dna.length(); i++) {
      availablePositions.add(i);
    }

    // Perform mutations
    for (int i = 0; i < numMutations; i++) {
      int randomPosition = random.nextInt(availablePositions.size());
      int position = availablePositions.remove(randomPosition);

      char originalBase = dna.charAt(position);
      char mutatedBase = getDifferentBase(originalBase);

      mutatedDna.setCharAt(position, mutatedBase);
    }

    return new DNATools(mutatedDna.toString());
  }

  /**
   * Returns a random base different from the original base.
   *
   * @param originalBase the original base to mutate from
   * @return a mutated base (different from the original)
   */
  public static char getDifferentBase(char originalBase) {
    
    if (originalBase == 'n' || originalBase == 'N') { // Unknown base in original DNA sequence
      return BASES[random.nextInt(BASES.length)]; // Randomly choose any known base
    }
    
    int index = new String(BASES).indexOf(originalBase);

    // Pick a random index that isn’t the same as the original
    int newIndex = (index + 1 + random.nextInt(BASES.length - 1)) % BASES.length;

    return BASES[newIndex];
  }

  /**
   * Generates 10 mutated DNA sequences, each with a specified number of mutations.
   * Writes the mutated sequences to a file.
   *
   * @param dnaString    original DNA sequence to mutate
   * @param numMutations the number of base mutations to apply to each mutated sequence
   * @param out          where to print text output to
   * @return a list of the mutated DNA sequences
   */
  public static void mutateAndWriteToFile(String dnaString, int numMutations, PrintStream out) {
    List<String> mutatedSequences = new ArrayList<>();

    // Create the initial DNATools instance
    DNATools dnaTools = new DNATools(dnaString);

    out.println("\nMutating DNA sequence...");

    // Generate 10 mutated sequences
    for (int i = 0; i < 10; i++) {
      DNATools mutatedDna = dnaTools.mutate(numMutations);
      mutatedSequences.add(mutatedDna.dna()); // Store the mutated DNA sequence to the list
    }

    // Dynamically generate the file name based on the current timestamp
    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String fileName = "mutated_dna_" + timestamp + ".fa";

    // Write the mutated sequences to a file
    try (FileWriter writer = new FileWriter(fileName)) {
      for (int i = 0; i < mutatedSequences.size(); i++) {
        writer.write(">mutation_" + (i + 1) + "\n");  // Write a header for each mutated sequence
        String mutatedSequence = mutatedSequences.get(i);
  
        // Write the sequence over multiple lines (e.g., 80 characters per line)
        for (int j = 0; j < mutatedSequence.length(); j += 80) {
          writer.write(mutatedSequence.substring(j, Math.min(j + 80, mutatedSequence.length())) + "\n");
        }
      }
      out.println("Mutated DNA sequences have been written to: " + fileName + "\n");
    } catch (IOException e) {
      out.println("Error writing to file: " + e.getMessage() + "\n");
    }
  }
}
