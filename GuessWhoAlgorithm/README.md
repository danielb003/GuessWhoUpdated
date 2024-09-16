# GuessWhoAlgorithm
Two algorithms to complete a game of guess who

# Instructions

Before attempting to run the program, please build a path to the jar file (jopt-simple-5.0.2.jar).
To build a path in Eclipse IDE, first import the project. Then right-click the project (make sure it's
a Java project) and select 'properties'. Go to 'Java build path', press 'add jars' and navigate to
the folder you are currently using. Inside you can then select the jopt-simple-5.0.2.jar file and
create the dependency.

After this you need to add in the command line arguements to run the program. There are a couple
variations including;
<br/>
-l sylog.txt ./sampleGameFiles/game1.config ./sampleGameFiles/game1.chosen binary binary
<br/>
-l sylog.txt ./sampleGameFiles/game1.config ./sampleGameFiles/game1.chosen binary random
<br/>
<br/>
Where the last two strings can interchange between binary or random. The last two strings
represent the type of player and you can either run the binary or random guessing algorithm
on that player.
<br/><br/>
The random arguement will use a random algorithm to play the game, whereas the binary arguement
will try it's best to make each guess split the remainding people on the guess who board into
two (elimate half). It then uses this strategy to solve the opposing player in as little turns
as possible.
