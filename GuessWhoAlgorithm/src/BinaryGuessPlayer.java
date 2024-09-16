import java.io.*;
import java.util.*;

/**
 * Binary-search based guessing player.
 * This player is for task C.
 * <p>
 * You may implement/extend other interfaces or classes, but ensure ultimately
 * that this class implements the Player interface (directly or indirectly).
 */
public class BinaryGuessPlayer implements Player {
    private ArrayList<String> people;
    private ArrayList<String> allThePeople;
    private HashMap<String, ArrayList<String>> attributeInnerMap;
    private HashMap<String, HashMap<String, ArrayList<String>>> attributeOuterMap;
    private HashMap<String, String> personInnerMap;
    private HashMap<String, HashMap<String, String>> personOuterMap;
    private HashMap<String, HashMap<String, String>> chosenNameOuterMap;
    private HashMap<String, String> correctAnswers;
    private HashMap<String, String> chosenNameInnerMap;

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
    public BinaryGuessPlayer(String gameFilename, String chosenName) throws IOException {
        //variable declaration
        attributeOuterMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
        allThePeople = new ArrayList<String>();
        personInnerMap = new HashMap<String, String>();
        personOuterMap = new HashMap<String, HashMap<String, String>>();
        chosenNameOuterMap = new HashMap<String, HashMap<String, String>>();
        chosenNameInnerMap = new HashMap<String, String>();
        correctAnswers = new HashMap<String, String>();


        try {
            //Open filename with Bufferreader
            FileReader fr = new FileReader(gameFilename);
            BufferedReader br = new BufferedReader(fr);

            //variable declaration
            String input;
            String person = null;
            Boolean attributefound = true;
            Boolean personfound = false;

            //Read through file line by line until the end
            while ((input = br.readLine()) != null) {
                //split content on line into string array
                String[] splited = input.split(" ");

                //attributes are at beginning of file so attribute found is true to start
                Outterloop:
                if (attributefound) {
                    //if new paragraph, break and set attributes to false.
                    if ("".equals(input.trim())) {
                        attributefound = false;
                        break Outterloop;
                    }

                    attributeInnerMap = new HashMap<String, ArrayList<String>>();//create a new map for attribute, person pair
                    for (int i = 1; i < splited.length; i++) {
                        people = new ArrayList<String>();//create an arrayList of people
                        attributeInnerMap.put(splited[i], people);//add each value as a key and arraylist as value

                    }
                    attributeOuterMap.put(splited[0], attributeInnerMap);//add attribute name as key to outter array container
                }
                //
                Outterloop:
                if (personfound) {
                    //if new paragraph, break and set personfound to false.
                    if ("".equals(input.trim())) {
                        personfound = false;
                        break Outterloop;
                    }
                    attributeOuterMap.get(splited[0]).get(splited[1]).add(person);//for each attribute and value pair, add the corresponding person to the inner arraylist for attributes


                    personInnerMap.put(splited[0], splited[1]); //map the attribute and value pair per person
                    personOuterMap.put(person, personInnerMap);//Map the persons name and add the attribute, value pair
                    if (person.equals(chosenName)) {// if the person being mapped currently is the person from .chosen file
                        chosenNameInnerMap.put(splited[0], splited[1]);//map the attribute and value pair of the chosen person separately
                        chosenNameOuterMap.put(person, chosenNameInnerMap);//Map the chosenpersons name and add the attribute, value pair
                        //if new paragraph, and set personfound to false.
                        if ("".equals(input.trim())) {
                            personfound = false;
                        }
                    }
                }
                if (input.contains("P")) //if the line input begins with P, person found set up input for next line
                {
                    attributefound = false;
                    personfound = true;
                    person = input;//record person name
                    allThePeople.add(person);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // end of BinaryGuessPlayer()

    /**
     * if the total number of people if odd, add 1 to the stored result to make it divisible to the ceilings result
     * if allThePeople(number of people remaining in potential guesses) is == 1, return guess of the remaining person.
     * iterate through all attribute and value pairs, if the amount of people in a given attribute and value pair == halfthepeople guess the attribute pair
     *
     * @return a guess of person or attribute type
     */
    public Guess guess() {
        String postAttribute = null;
        String postValue = null;
        int halfThePeople;
        if (allThePeople.size() % 2 != 0)
            halfThePeople = (int) Math.ceil((allThePeople.size() + 1) / 2);
        else
            halfThePeople = (int) Math.ceil(allThePeople.size() / 2);
        if (allThePeople.size() == 1) // if only 1 person remains to guess
            return new Guess(Guess.GuessType.Person, "", allThePeople.get(0).toString());//guess the last remaining person
        for (String attr : this.getAttributeOuterMap().keySet()) { //get Attribute names
            for (String val : this.getAttributeOuterMap().get(attr).keySet()) { //get corresponding values for the above attributes
                if (this.getAttributeOuterMap().get(attr).get(val).size() == halfThePeople) {  //if the attr/val pairs people == half the people
                    postAttribute = attr;
                    postValue = val;
                }
            }
        }
        return new Guess(Guess.GuessType.Attribute, postAttribute, postValue);
    } // end of guess()

    /**
     * Recieve guess based on guess type
     *
     * @param currGuess Opponent's guess.
     * @return the result, true or false for guess matching the players person
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
     * recieves a answer from player as a result of the previous guess.
     * If the answer is true add correct guess attribute and value to a list of correct guesses.
     * updateAttributeMap for guess details and answer
     * remove attribute from possible guesses.
     *
     * @param currGuess This player's initial guess.
     * @param answer    Opponent's answer to guess.
     * @return false in all other scenarios
     */
    public boolean receiveAnswer(Guess currGuess, boolean answer) {
        String type = currGuess.getType().toString();
        if (type.equals("Attribute")) {
            if (answer) {
                //remove all other attribute values
                //ie. if faceShape white == true, then remove faceShape black green red blue brown
                //also remove people from other attributes who dont have this attribute
                correctAnswers.put(currGuess.getAttribute(), currGuess.getValue());
                updateAttributeMap(currGuess, "true");
                this.getAttributeOuterMap().remove(currGuess.getAttribute());
            } else {
                //remove this attribute & the people who have this attribute == true
                updateAttributeMap(currGuess, "false");
                this.getAttributeOuterMap().remove(currGuess.getAttribute());
            }
        } else if (type.equals("Person")) {
            if (answer)
                return true;
        }
        return false;
    } // end of receiveAnswer()

    public HashMap<String, HashMap<String, String>> getChosenNameOuterMap() {
        return chosenNameOuterMap;
    }

    public HashMap<String, HashMap<String, ArrayList<String>>> getAttributeOuterMap() {
        return attributeOuterMap;
    }

    /**
     * Updates the data structure of existing attributes, values and people.
     *
     * @param currGuess
     * @param answer
     */
    public void updateAttributeMap(Guess currGuess, String answer) {
        String currAttribute = currGuess.getAttribute();
        String currValue = currGuess.getValue();

        for (String attr : this.getAttributeOuterMap().keySet()) { //get key values of all attributes
            for (String val : this.getAttributeOuterMap().get(attr).keySet()) { // for each attribute, get values
                for (int i = 0; i < this.getAttributeOuterMap().get(attr).get(val).size(); i++) { // iterate through each attribute, value pair
                    int count = 0;
                    for (int k = 0; k < this.getAttributeOuterMap().get(currAttribute).get(currValue).size(); k++) { //iterate through out desired attribute, value pair
                        String personInMap = this.getAttributeOuterMap().get(attr).get(val).get(i).toString();
                        String desiredPerson = this.getAttributeOuterMap().get(currGuess.getAttribute()).get(currGuess.getValue()).get(k).toString();

                        if (answer.equals("true")) {
                            if (!personInMap.equals(desiredPerson)) {
                                if (personInMap != null)
                                    count++;
                                if (count == this.getAttributeOuterMap().get(currGuess.getAttribute()).get(currGuess.getValue()).size()) {
                                    if (allThePeople.contains(personInMap)) {
                                        allThePeople.remove(personInMap);
                                    }
                                    this.getAttributeOuterMap().get(attr).get(val).remove(i);
                                }
                            }
                        } else if (answer.equals("false")) {
                            if (personInMap.equals(desiredPerson)) {
                                if (personInMap != null)
                                    count++;
                                if (count == 1) {
                                    if (allThePeople.contains(personInMap)) {
                                        allThePeople.remove(personInMap);
                                    }
                                    this.getAttributeOuterMap().get(attr).get(val).remove(i);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} // end of class BinaryGuessPlayer
