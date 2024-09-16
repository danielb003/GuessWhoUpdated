import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * Random guessing player.
 * This player is for task B.
 * <p>
 * You may implement/extend other interfaces or classes, but ensure ultimately
 * that this class implements the Player interface (directly or indirectly).
 */
public class RandomGuessPlayer implements Player {
    private HashMap<String, String> personInnerMap;
    private HashMap<String, HashMap<String, String>> personOuterMap;
    private HashMap<String, HashMap<String, String>> chosenNameOuterMap;
    private HashMap<String, String> correctAnswers;
    private HashMap<String, List<String>> attributeList;

    private Random generator;

    /**
     * Loads the game configuration from gameFilename, and also store the chosen
     * person.
     *
     * @param gameFilename Filename of game configuration.
     * @param chosenName   Name of the chosen person for this player.
     * @throws IOException If there are IO issues with loading of gameFilename.
     *                     Note you can handle IOException within the constructor and remove
     *                     the "throws IOException" method specification, but make sure your
     *                     implementation exits gracefully if an IOException is thrown.
     */
    public RandomGuessPlayer(String gameFilename, String chosenName) throws IOException {
        personOuterMap = new HashMap<String, HashMap<String, String>>();
        chosenNameOuterMap = new HashMap<String, HashMap<String, String>>();
        HashMap<String, String> chosenNameInnerMap = new HashMap<String, String>();

        correctAnswers = new HashMap<String, String>();

        attributeList = new HashMap<>();

        try {
            FileReader fr = new FileReader(gameFilename);
            BufferedReader br = new BufferedReader(fr);

            Boolean personfound = false;
            Boolean attributefound = true;
            String input;
            String person = null;

            while ((input = br.readLine()) != null) {
                String[] splited = input.split(" ");
                Outterloop:
                if (attributefound) {
                    if ("".equals(input.trim())) {
                        attributefound = false;
                        break Outterloop;
                    }
                    ArrayList<String> valueList;
                    valueList = new ArrayList<String>(Arrays.asList(splited).subList(1, splited.length));
                    attributeList.put(splited[0], valueList);
                }
                Outterloop:
                if (personfound) {
                    if ("".equals(input.trim())) {
                        personfound = false;
                        break Outterloop;
                    }
                    personInnerMap.put(splited[0], splited[1]);
                    personOuterMap.put(person, personInnerMap);
                    if (person.equals(chosenName)) {
                        chosenNameInnerMap.put(splited[0], splited[1]);
                        chosenNameOuterMap.put(person, chosenNameInnerMap);
                        if ("".equals(input.trim())) {
                            personfound = false;
                        }
                    }
                }
                if (input.contains("P")) {
                    personInnerMap = new HashMap<String, String>();
                    personfound = true;
                    person = input;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        generator = new Random();
    } // end of RandomGuessPlayer()

    /**
     * Generates guesses for attributes until a definite person guess can be made.
     * A random Attribute and value pair are returned from respective methods returned
     *
     * @return new Guess
     */
    public Guess guess() {
        if (guessPersonGenerator() != null) {
            String person = guessPersonGenerator();
            return new Guess(Guess.GuessType.Person, "", person);
        }
        if (this.getAttributeList().isEmpty()) {
            String person = guessPersonGenerator();
            return new Guess(Guess.GuessType.Person, "", person);
        }

        String postAttribute = guessAttributeGenerator();
        String postValue = guessValueGenerator(postAttribute);

        return new Guess(Guess.GuessType.Attribute, postAttribute, postValue);
    } // end of guess()

    /**
     * Method checks if currGess is of type person and attribute and compaired with the players chosen person/attributes
     * String Object created to hold the answering players chosenPerson and attributes with some styling removed.
     *
     * @param currGuess Opponent's guess.
     * @return false if it does not.
     */
    public boolean answer(Guess currGuess) {
        String individual = this.getChosenNameOuterMap().keySet().toString();
        individual = individual.replace("]", "");
        individual = individual.replace("[", "");
        String type = currGuess.getType().toString();

        if (type.equals("Person")) {
            return individual.equals(currGuess.getValue());
        } else if (type.equals("Attribute")) {
            return (this.getChosenNameOuterMap().get(individual).get(currGuess.getAttribute())).equals(currGuess.getValue());
        }
        return false;
    } // end of answer()

    /**
     * Splits guesses by type person or attribute
     * Attribute: if answer is received as true then the correct attribute is added to a list of correct answers for testing on potential person guesses,
     * all values of the same attribute are removed from the attribute list so that no further guesses can be made on the correctly guess attribute
     *
     * @param currGuess This player's initial guess.
     * @param answer    Opponent's answer to guess.
     * @return false in all other scenarios
     */
    public boolean receiveAnswer(Guess currGuess, boolean answer) {
        String individual = this.getChosenNameOuterMap().keySet().toString();
        individual = individual.replace("]", "");
        individual = individual.replace("[", "");

        String type = currGuess.getType().toString();

        if (type.equals("Attribute")) {
            if (answer) {
                correctAnswers.put(currGuess.getAttribute(), currGuess.getValue());
                this.getAttributeList().remove(currGuess.getAttribute());
            } else {
                //remove from values
                this.getAttributeList().get(currGuess.getAttribute()).remove(currGuess.getValue());
            }
        } else if (type.equals("Person")) {
            if (answer)
                return true;
            else
                getPersonOuterMap().remove(currGuess.getValue());
        }
        return false;
    } // end of receiveAnswer()

    private HashMap<String, List<String>> getAttributeList() {
        return attributeList;
    }

    private HashMap<String, HashMap<String, String>> getChosenNameOuterMap() {
        return chosenNameOuterMap;
    }

    private HashMap<String, HashMap<String, String>> getPersonOuterMap() {
        return personOuterMap;
    }

    private String guessAttributeGenerator() {
        Object[] attributes = this.getAttributeList().keySet().toArray();

        Object randomAttribute = attributes[generator.nextInt(attributes.length)];

        return randomAttribute.toString();
    }

    /**
     * This method is used to generate a guess type of Attribute
     * a set of prior confirmed attribute values are
     * returns when an associated attribute is passed in
     *
     * @param attribute is returned by guessAttributeGenerator()
     * @return String
     */
    private String guessValueGenerator(String attribute) {
        //create array with list or current attributes
        Object[] values = this.getAttributeList().get(attribute).toArray();
        Object randomValue = values[generator.nextInt(values.length)];

        return randomValue.toString();
    }

    /**
     * This method is used to generate a guess type of person.
     * a set of prior confirmed correct attribute guesses are
     * compared with the set of people and their attributes
     *
     * @return String This returns String of Px where 'x' is the
     * corresponding integer matching the given attributes.
     */
    private String guessPersonGenerator() {
        int k = 0;
        String P;
        //itterate through names
        ArrayList<String> matches = new ArrayList<String>();
        for (String Px : this.getPersonOuterMap().keySet()) {
            int i = 1;
            //itterate through attributes
            for (String attributes : this.getPersonOuterMap().get(Px).keySet()) {
                String personAttributes = this.getPersonOuterMap().get(Px).get(attributes);
                String answerAttributes = this.correctAnswers.get(attributes);

                if (personAttributes.equals(answerAttributes)) {
                    i++;
                    if (i == this.correctAnswers.size()) {
                        matches.add(Px);
//                            return Px;
                    }
                }
            }
        }
        if (matches.size() == 1) {
            String match = matches.toString();
            match = match.replace("[", "");
            match = match.replace("]", "");
            return match;
        }
        return null;
    }
} // end of class RandomGuessPlayer
