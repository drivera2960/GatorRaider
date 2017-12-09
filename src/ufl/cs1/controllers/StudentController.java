package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.controllers.benchmark.Devastator;
import game.models.*;
import game.view.GameView;
import game.models.Actor;
import game.models.Maze;
import java.util.List;

public final class StudentController implements DefenderController {

	public void init(Game game) { }

	public void shutdown(Game game) { }

	public int[] update(Game game, long timeDue) {

		int[] actions = new int[Game.NUM_DEFENDER];             //An int array of size 4, that holds the next action of each ghost.

		Actor MsPac = game.getAttacker();                       //Creates an Actor object of Ms. Pacman aka MsPac aka Devastator

		Defender blinky = game.getDefender(0);		//Creates a Defender object of the RED ghost
		Defender pinky = game.getDefender(1);		//Creates a Defender object of the PINK ghost
		Defender inky = game.getDefender(2);		//Creates a Defender object of the ORANGE ghost
		Defender sue = game.getDefender (3);		//Creates a Defender object of the BLUE ghost
		Attacker attacker = game.getAttacker();

		List<Defender> inkysFriends = game.getDefenders();      //List of ghosts NOT including Inky
		inkysFriends.remove(2);

		actions[0] = blinkyAlgorithm(game, MsPac, blinky);      //Calls blinkyAlgorithm, which returns the next direction Blinky will take.
		actions[1] = pinkyAlgorithm(MsPac, pinky);        	    //Calls pinkyAlgorithm, which returns the next direction Pinky will take.
//		actions[2] = inkyAlgorithm(game, MsPac, inky);          //Calls inkyAlgorithm, which returns the next direction Inky will take.
                                                                // Use this particular method call for the placeholder inkyAlgorithm
		actions[2] = inkyAlgorithm(game, attacker, inky, inkysFriends);        //Use method call for the soon to be completed, final inkyAlgorithm

        actions[3] = sueAlgorithm(game, MsPac, sue);            //Calls sueAlgorithm, which returns the next direction Sue will take.

		//scatter(actions, blinky, pinky, inky, sue);           //Potential scatter function


		return actions;

	}
	//Coded by David Rivera
    //This method is for blinky's algorithm.  Blinky calculates the nearest power pill to him and travels to it.  He does this
    //because Ms. MsPac is always trying to eat the power pills in order to eat the ghosts, and get more points.  By sending
    //Blinky to the closest power pill, he guards it, however, he is vulnerable to Ms. Pacman if she gets behind him.  To counter this,
    //Blinky calculates the distance between himself and Ms. Pacman, and if she comes near him, he will turn around and chase her.
    //If Ms. Pacman manages to eat all the power pills, Blinky will resort to chasing her indefinitely, as there is no more Power Pills
    //to guard.
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


	//Coded by Nishtha Garg
    //This method is for Pinky's algorithm.  Pinky is always chasing Ms. Pacman, as long as she is invulnerable.  While,
    //she is not able to kill Ms. Pacman by on her own (as she and Ms. Pacman have the same speed), she is crucial when the ghosts attempt
    //to surround Ms. Pacman and "sandwich" her.  She also acts as a decoy.  Given that Ms. Pacman is always trying to eat the ghosts,
    //she is always trying to get to the power pills when a ghost is near her.  Having Pinky behind her causes her to head toward a power pill,
    //often with only Pinky in direct pursuit.  This limits the amount of ghosts eaten each time Ms. Pacman consumes a power pill,
    //resulting in her scoring less points.
    //In addition, when Pinky becomes vulnerable, she attempts to flee from Ms. Pacman.
	public int pinkyAlgorithm(Actor MsPac, Defender pinky){
			if (pinky.isVulnerable()) { //Checks if Pinky is vulnerable.
				return pinky.getNextDir(MsPac.getLocation(), false);    //IF Pinky is vulnerable, she attempts to flee.
			} else
				return pinky.getNextDir(MsPac.getLocation(), true);     //IF Pinky is not vulnerable, she chases Ms. Pacman.
	}





    public int inkyAlgorithm (Game game, Attacker MsPac, Defender inky, List<Defender> inkysFriends) {
        // Inky is the MsPac DoubleCrosser - It does the scatter function at a junction
        int action = -1; // Default value - Don't move
        List<Integer> possibleDirs = inky.getPossibleDirs(); // Get valid ghost moves

        if (possibleDirs.size() != 0) { // If ghost is out of lair
            if(!inky.isVulnerable()) { //If ghost is not vulnerable

                // Chase MsPac or double-cross MsPac if possible
                boolean ghostInPath = false;
                // get the path to MsPac
                List<Node> pathToDevastator = inky.getPathTo(MsPac.getLocation());
                // Find the nearest node on the path
                Node followDevastator = inky.getTargetNode(pathToDevastator, true);

                // check if another ghost is in the same path leading to MsPac
                for (int i = 0; i < pathToDevastator.size(); i++) {
                    for (int j = 0; j < inkysFriends.size(); j++) {
                        if (inkysFriends.get(j).getLocation() == pathToDevastator.get(i) && !inkysFriends.get(j).isVulnerable()){
                            ghostInPath = true;
                            break;
                        }
                    }
                }

                // if ghost in the same path and inky is at a junction
                boolean nullFlag = false;
                if (ghostInPath && inky.getLocation().isJunction() && inky.getLocation().getPathDistance(MsPac.getLocation())<=50){
                    List<Node> neighbors = inky.getPossibleLocations(); // excludes opposite direction
                    //System.out.println("Neighbors: " +neighbors);

                    while (nullFlag == false){
                        Node flagger = neighbors.get(game.rng.nextInt(neighbors.size()));
                        if(flagger != null){
                            action = inky.getNextDir(flagger, true);
                            nullFlag = true;
                        }
                    }
                } else {
                    //follow that path
                    action = inky.getNextDir(MsPac.getLocation(), true);
                }

            } else {
                // Ghost is vulnerable - Run away from MsPac
                action = inky.getNextDir(MsPac.getLocation(), false);
            }
        }

        return action;
    }



//Below is a commented out, work in progress, method for Inky.  Written by Lawrence and Dave.
///////////////////////////////////////////////////////////////////////////////////
/*
	public int inkyAlgorithm(Game game, Actor MsPac, Defender inky, List<Defender> inkysFriends) {

		boolean sharedPath = false;
		if(inky.getPossibleDirs().size() !=0) {
			List <Node> pathToDestruction = inky.getPathTo(MsPac.getLocation());

			for(int i = 0; i<inkysFriends.size();++i) {
				for (int j = 0; j < pathToDestruction.size(); ++j) {
					if (inkysFriends.get(i).getLocation() == pathToDestruction.get(j) )
						sharedPath = true;
				}
			}
			if(!(sharedPath)){
				if(inky.getLocation().isJunction()){
					List <Node> possibleLoc = inky.getLocation().getNeighbors();


			}else
				return inky.getNextDir(MsPac.getLocation(), true);

		}else
			return -1;

	}
	*/
////////////////////////////////////////////////////////////////////////////////////////////

    /*
    //This method for inky is a placeholder
    //Final code will be made by Lawrence and Dave
	public int inkyAlgorithm(Game game, Actor MsPac, Defender inky) {
		Node nullFlag = null;
		Attacker attacker = game.getAttacker();

		if(inky.getPossibleDirs().size() !=0) {
			//if (!(inky.isVulnerable())) {
			//	return inky.getNextDir(inky.getTargetNode(attacker.getPossibleLocations(false),true),true);
			if (!(inky.isVulnerable())) {
				if(MsPac.getLocation().getNeighbor(MsPac.getDirection()) != nullFlag){
					return inky.getNextDir( MsPac.getLocation().getNeighbor(MsPac.getDirection() ),true );
				} else
					return inky.getNextDir(MsPac.getLocation(), true);

			}else
				return inky.getNextDir(MsPac.getLocation(), false);

		}else
			return -1;
	}
	*/

    //This method is simply a placeholder.  Final code will be coded by Christian
	public int sueAlgorithm(Game game, Actor MsPac, Defender sue){
		if(sue.getPossibleDirs().size() != 0){
			if(!(sue.isVulnerable())){
				if(game.getPillList().size() != 0){
					if (sue.getLocation().getPathDistance(MsPac.getLocation()) <=60)
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

//Below System.out(s) were made to display different information.  These were used to get an understanding of how method calls
//would be implemented in the project.  These will be removed.
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/*
System.out.print(game.getCurMaze().getPowerPillNodes().get(0).getX()+ ", ");
System.out.println(game.getCurMaze().getPowerPillNodes().get(0).getY());
System.out.print(game.getCurMaze().getPowerPillNodes().get(1).getX()+ ", ");
System.out.println(game.getCurMaze().getPowerPillNodes().get(1).getY());
System.out.print(game.getCurMaze().getPowerPillNodes().get(2).getX()+ ", ");
System.out.println(game.getCurMaze().getPowerPillNodes().get(2).getY());
System.out.print(game.getCurMaze().getPowerPillNodes().get(3).getX()+ ", ");
System.out.println(game.getCurMaze().getPowerPillNodes().get(3).getY());
*/


//System.out.println( MsPac.getLocation() );
//System.out.print(ghost1.getLocation().getX()+", ");
//System.out.println(ghost1.getLocation().getY());
//Node capture = game.getCurMaze().getPowerPillNodes().get(3);


//Actor blinky = ghost1;
//actions[0] = ghost1.getPossibleDirs().get(Game.rng.nextInt(ghost1.getPossibleDirs().size()));
//actions[0] = ghost1.getNextDir(capture,true);


//System.out.println(blinky.getLocation().getPathDistance(MsPac.getLocation()));
//System.out.println(MsPac.getLocation().getNeighbor(MsPac.getDirection() ));
//System.out.println(attacker.getPossibleLocations(false));
