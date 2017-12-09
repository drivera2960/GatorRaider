package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.models.*;
import java.util.List;

public final class StudentController implements DefenderController {

	public void init(Game game) { }

	public void shutdown(Game game) { }

	public int[] update(Game game, long timeDue) {

		int[] actions = new int[Game.NUM_DEFENDER];             // An int array of size 4, that holds the next action of each ghost.

		Actor MsPac = game.getAttacker();                       // Creates an Actor object of Ms. Pacman aka MsPac aka Devastator

		Defender blinky = game.getDefender(0);		// Creates a Defender object of the RED ghost
		Defender pinky = game.getDefender(1);		// Creates a Defender object of the PINK ghost
		Defender inky = game.getDefender(2);		// Creates a Defender object of the ORANGE ghost
		Defender sue = game.getDefender (3);		// Creates a Defender object of the BLUE ghost
		Attacker attacker = game.getAttacker();

		actions[0] = blinkyAlgorithm(game, MsPac, blinky);      // Calls blinkyAlgorithm, which returns the next direction Blinky will take.
		actions[1] = pinkyAlgorithm(MsPac, pinky);        	    // Calls pinkyAlgorithm, which returns the next direction Pinky will take.
		actions[2] = inkyAlgorithm(game, attacker, inky);       // Calls inkyAlgorithm, which returns the next direction Inky will take.
        actions[3] = sueAlgorithm(game, MsPac, sue);            // Calls sueAlgorithm, which returns the next direction Sue will take.

		return actions;
	}

	// Coded by David Rivera
    // This method is for Blinky's algorithm.  Blinky finds the power pill nearest to him and travels to it.  He does this
    // because Ms. MsPac is always trying to eat the power pills in order to eat the ghosts, and get more points.
    // By sending Blinky to the nearest PowerPill, he becomes a PowerPill guardian, however, he is vulnerable to Ms. Pacman if she gets in behind him.
	// To counter this, Blinky calculates the distance between himself and Ms. Pacman, and if she comes near him, he will turn around to chase her.
    // If Ms. Pacman manages to eat all the power pills, Blinky will resort to chasing her indefinitely, as there are no more PowerPills left to guard.
	// When Blinky becomes vulnerable, she attempts to flee from Ms. Pacman.

	public int blinkyAlgorithm(Game game, Actor MsPac,Defender blinky){
		if (blinky.getPossibleDirs().size() != 0) {     //Checks IF Blinky is able to move
			if(game.getPowerPillList().size() != 0) {   //Checks IF there are any remaining power pills on the map.
				if (!(blinky.isVulnerable())) {		    //Checks IF Blinky is not vulnerable
                    //The following IF statement checks how close MsPac is to Blinky.  If they are of length 40 apart,
                    //then Blinky turns around to chase MsPac.
                    //Otherwise Blinky will continue to go to the nearest power pill.
					if (blinky.getLocation().getPathDistance(MsPac.getLocation()) <= 40){
						return blinky.getNextDir(MsPac.getLocation(),true);
					} else
						return blinky.getNextDir(blinky.getTargetNode(game.getPowerPillList(), true), true);
				} else  //IF Blinky is vulnerable, he tries to run away as far as possible as MsPac
					return blinky.getNextDir(MsPac.getLocation(),false);

			} else //If there are no more power pills, Blinky chases MsPac.
				return blinky.getNextDir(MsPac.getLocation(),true);

		} else     //If no directions are possible, -1 is returned.
			return -1;
	}

	// Coded by Nishtha Garg
    // This method is for Pinky's algorithm.  Pinky is always chasing Ms. Pacman, as long as she is invulnerable.  While,
    // she is not able to kill Ms. Pacman on her own (since she and Ms. Pacman have the same speed), she is crucial when the ghosts attempt
    // to surround Ms. Pacman and "sandwich" her.  She also acts as a decoy.  Given that Ms. Pacman is always trying to eat the ghosts,
    // she is always trying to get to the power pills when a ghost is near her.  Having Pinky behind her causes her to head toward a power pill,
    // often with only Pinky in direct pursuit.  This limits the amount of ghosts eaten each time Ms. Pacman consumes a power pill,
    // resulting in her scoring less points.
    // In addition, when Pinky becomes vulnerable, she attempts to flee from Ms. Pacman.

	public int pinkyAlgorithm(Actor MsPac, Defender pinky){
			if (pinky.isVulnerable()) { //Checks if Pinky is vulnerable.
				return pinky.getNextDir(MsPac.getLocation(), false);    //IF Pinky is vulnerable, she attempts to flee.
			} else
				return pinky.getNextDir(MsPac.getLocation(), true);     //IF Pinky is not vulnerable, she chases Ms. Pacman.
	}

	// Coded by Layiwola Ibukun
	// This method is for Inky's algorithm. Inky is the smartest ghost on the team. Inky tries to double cross Ms. Pacman when the conditions are right.
	// If Inky is faraway from Ms. Pacman, he chases after the node in front of her. When Inky becomes reasonable close to Ms. Pacman, he makes a decision
	// based on his position relative to hers. If she is facing him, he goes after her head on, but if she turns her back on him, he decides to
	// get in front of her face by travelling in a different direction at the next junction. The direction that Inky chooses in order to double cross
	// Ms. Pacman is fairly random because there might be more than one way to do so. It also prevents the possibility of a circular movement or
	// infinite loop behaviour where a subset of decisions keep repeating themselves.
	// If there is no node in front of Ms. Pacman, Inky will chase after her instead, avoiding a nullPointerException that could crash the program.
	// Of course, when Inky becomes vulnerable, he attempts to flee from Ms. Pacman.

	public int inkyAlgorithm(Game game, Attacker MsPac, Defender inky) {
		// Inky is the pacman DoubleCrosser - It does the scatter function at a junction
		int action = -1; // Default value - Don't move

		if (inky.getPossibleDirs().size() != 0) {		// Checks IF INKY is able to move
			if (!inky.isVulnerable()) {					// IF Inky is not vulnerable
				Node inFrontOfPacman = MsPac.getLocation().getNeighbor(MsPac.getDirection()); //get the node in front of MsPac
				if (inFrontOfPacman != null) {
					action = inky.getNextDir(inFrontOfPacman, true);		// Default value
					List<Node> pathToVictory = inky.getPathTo(inFrontOfPacman);		// List of nodes leading in front of MsPac

					for (int i = 0; i < pathToVictory.size(); i++) {
						// Below IF checks if MsPac is in the way, and if Inky is at a junction, and if MsPac is near Inky
						if (MsPac.getLocation() == pathToVictory.get(i) && inky.getLocation().isJunction() && inky.getPathTo(MsPac.getLocation()).size() <= 50) {
							List<Node> neighbors = inky.getPossibleLocations(); // Excludes Inky's opposite direction
							Node victory = null;		// Creates a null "flag"
							while (victory == null) {	// Psuedo-randomly chooses a non-null direction to double cross MsPac
								victory = neighbors.get(game.rng.nextInt(neighbors.size()));
							}
							action = inky.getNextDir(victory, true);
						}
					}
				} else
					action = inky.getNextDir(MsPac.getLocation(), true); // Go after MsPac herself if the space in front of her is null
			} else
				action = inky.getNextDir(MsPac.getLocation(), false); // Runaway if Inky is Vulnerable
		}
		return action;
	}

    //This method is simply a placeholder.  Final code will be coded by Christian

	public int sueAlgorithm(Game game, Actor MsPac, Defender sue){
		if(sue.getPossibleDirs().size() != 0){
			if(!(sue.isVulnerable())){
				if(game.getPillList().size() != 0){
					if (sue.getLocation().getPathDistance(MsPac.getLocation()) <=50)
						return sue.getNextDir(MsPac.getLocation(),true);
					else
					return sue.getNextDir(sue.getTargetNode(game.getPillList(), true), true);
				}else
					return sue.getNextDir(MsPac.getLocation(),true);
			}else
				return sue.getNextDir(MsPac.getLocation(), false);

		}else
			return -1;
	}

}