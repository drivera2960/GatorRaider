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

		actions[0] = blinkyAlgorithim(game, MsPac, blinky);
		actions[1] = pinkyAlgorithim(game, MsPac, pinky);
//		actions[2] = inkyAlgorithim(game, MsPac, inky);

		actions[2] = inkyAlgorithim(game, MsPac, inky, inkysFriends);
		actions[3] = sueAlgorithim(game, MsPac, sue);

		//scatter(actions, blinky, pinky, inky, sue);

		//System.out.println(blinky.getLocation().getPathDistance(MsPac.getLocation()));
		//System.out.println(MsPac.getLocation().getNeighbor(MsPac.getDirection() ));
		//System.out.println(attacker.getPossibleLocations(false));
		return actions;

	}
	//power pill ghost
	public int blinkyAlgorithim(Game game, Actor MsPac,Defender blinky){
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

	public int pinkyAlgorithim(Game game, Actor MsPac, Defender pinky){
		if (pinky.getPossibleDirs().size() != 0) {
			if (!(pinky.isVulnerable())) {
				return pinky.getNextDir(MsPac.getLocation(), true);
			} else
				return pinky.getNextDir(MsPac.getLocation(), false);
		}else
			return -1;
	}


	public int inkyAlgorithim(Game game, Actor MsPac, Defender inky, List<Defender> inkysFriends) {
// Inky is the pacman DoubleCrosser - It does the scatter function at a junction
		int action = -1; // Default value - Don't move
		List<Integer> possibleDirs = inky.getPossibleDirs(); // Get valid ghost moves

		if (possibleDirs.size() != 0) { // If ghost is out of lair
			if(!inky.isVulnerable()) { //If ghost is not vulnerable

				// Chase pacman or double-cross pacman if possible
				boolean ghostInPath = false;
				// get the path to pacman
				List<Node> pathToDevastator = inky.getPathTo(MsPac.getLocation());
				// Find the nearest node on the path
				Node followDevastator = inky.getTargetNode(pathToDevastator, true);

				// check if another ghost is in the same path leading to pacman
				for (int i = 0; i < pathToDevastator.size(); i++) {
					for (int j = 0; j < inkysFriends.size(); j++) {
						if (inkysFriends.get(j).getLocation() == pathToDevastator.get(i) && !inkysFriends.get(j).isVulnerable()){
							ghostInPath = true;
							break;
						}
					}
				}

				// if ghost in the same path and inky is at a junction
				if (ghostInPath && inky.getLocation().isJunction()){
					List<Node> neighbors = inky.getPossibleLocations(); // excludes opposite direction
					for (int j = 0; j < neighbors.size(); j++) {
						if (neighbors.get(j) != followDevastator){
							// take a different path
							action = inky.getNextDir(neighbors.get(j), true);
						}
					}
				} else {
					//follow that path
					action = inky.getNextDir(followDevastator, true);
				}

			} else {
				// Ghost is vulnerable - Run away from pacman
				action = inky.getNextDir(MsPac.getLocation(), false);
			}
		}

		return action;

	}






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
/*
/////////////////////////////////////////////////////////////////////////////////////////////////////
	//orange ghost; this one works if uncommeted
	public int inkyAlgorithim(Game game, Actor MsPac, Defender inky) {
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
///////////////////////////////////////////////////////////////////////////////////////////////////
*/


	//blue ghost
	public int sueAlgorithim(Game game, Actor MsPac, Defender sue){

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
//System.out.println( MsPac.getLocation() );
//System.out.print(ghost1.getLocation().getX()+", ");
//System.out.println(ghost1.getLocation().getY());
//Node capture = game.getCurMaze().getPowerPillNodes().get(3);



//Actor blinky = ghost1;
//actions[0] = ghost1.getPossibleDirs().get(Game.rng.nextInt(ghost1.getPossibleDirs().size()));
//actions[0] = ghost1.getNextDir(capture,true);

