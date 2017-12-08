package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.controllers.benchmark.Devastator;
import game.models.*;
import game.view.GameView;
import game.models.Actor;
import game.models.Maze;
import java.util.List;

public final class StudentController implements DefenderController {

	public void init(Game game) {
	}

	public void shutdown(Game game) {
	}

	public int[] update(Game game, long timeDue) {
		int[] actions = new int[Game.NUM_DEFENDER];

		Actor MsPac = game.getAttacker();

		Defender blinky = game.getDefender(0);		//red ghost
		Defender pinky = game.getDefender(1);		//pink ghost
		Defender inky = game.getDefender(2);		//orange ghost
		Defender sue = game.getDefender (3);		//blue ghost
		Attacker attacker = game.getAttacker();

		List<Defender> inkysFriends = game.getDefenders();
		inkysFriends.remove(2);

		actions[0] = blinkyAlgorithm(game, MsPac, blinky);
		actions[1] = pinkyAlgorithm(game, MsPac, pinky);
		actions[2] = inkyAlgorithm(game, MsPac, inky);                      //Use this method call for the placeholder inkyAlgorithm
//		actions[2] = inkyAlgorithm(game, MsPac, inky, inkysFriends);        //Use method call for completed inkyAlgorithim
		actions[3] = sueAlgorithm(game, MsPac, sue);

		//scatter(actions, blinky, pinky, inky, sue);


		return actions;

	}
	//Coded by David Rivera
	public int blinkyAlgorithm(Game game, Actor MsPac,Defender blinky){
		if (blinky.getPossibleDirs().size() != 0) {
			if(game.getPowerPillList().size() != 0) {
				if (!(blinky.isVulnerable())) {											//blinky is not vulnerable
					if (blinky.getLocation().getPathDistance(MsPac.getLocation()) <= 40){
						return blinky.getNextDir(MsPac.getLocation(),true);
					} else
						return blinky.getNextDir(blinky.getTargetNode(game.getPowerPillList(), true), true);
				} else
					return blinky.getNextDir(MsPac.getLocation(),false);

			} else
				return blinky.getNextDir(MsPac.getLocation(),true);

		} else
			return -1;
	}


	//Coded by Nishtha Garg
	public int pinkyAlgorithm(Game game, Actor MsPac, Defender pinky){
			if (pinky.isVulnerable()) {
				return pinky.getNextDir(MsPac.getLocation(), false);
			} else
				return pinky.getNextDir(MsPac.getLocation(), true);
	}



//Below commented out method call is a work in progress for inky.  Written by Lawrence and Dave.
///////////////////////////////////////////////////////////////////////////////////
/*
	public int inkyAlgorithim(Game game, Actor MsPac, Defender inky, List<Defender> inkysFriends) {

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
//would be implemented.
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
