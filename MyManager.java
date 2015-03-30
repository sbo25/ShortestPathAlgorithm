package student;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import game.Edge;
import game.Manager;
import game.Node;
import game.Parcel;
import game.Truck;

public class MyManager extends game.Manager {
	
	private boolean weDone;
	
	
	@Override
	public void run() {
		ArrayList<Truck> trucks = new ArrayList<Truck>(getGame().getBoard().getTrucks());
		HashSet<Parcel> parcels = new HashSet<Parcel>(getGame().getBoard().getParcels());
		
		
		int size = trucks.size();
		
		for (Truck t : trucks) {
			ArrayList<Parcel> data = new ArrayList<Parcel>();
			t.setUserData(data);
		}
		int x = 0;
		for (Parcel p : parcels) {
			int index = ((int) Math.floor(Math.random()*size));
			for (int i = 0; i < 20; i++) {
				index = ((int) Math.floor(Math.random()*size));
				if (trucks.get(index).getColor() == p.getColor()) {
					break;
				}
				else {}
			}
			ArrayList<Parcel> newData = (ArrayList<Parcel>) trucks.get(index).getUserData();
			newData.add(p);
			trucks.get(index).setUserData(newData);
		}

		weDone = true;

	}

	@Override
	public void truckNotification(Truck t, Notification message) {
		ArrayList<Parcel> check = (ArrayList<Parcel>) t.getUserData();
		
		
		if (!weDone) {
			return;
		}
		
		
		if (message == Manager.Notification.WAITING) {
		if (!check.isEmpty()) {
			if (t.getLoad() != null) {
				
				if(t.getLocation() == t.getLoad().destination) {
				check.remove(check.indexOf(t.getLoad()));
				t.setUserData(check);
				t.dropoffLoad();
				
				}
				else {
					try {
						setShortestPath(t, t.getLocation(), t.getLoad().destination);
					}
					catch (NullPointerException e) {
						truckNotification(t, Manager.Notification.WAITING);
					}
					}
			}
			else {
				
				if (t.getLocation() == check.get(0).getLocation()) {
					
					t.pickupLoad(check.get(0));
				
				}
				else {
					try {
						setShortestPath(t, t.getLocation(), check.get(0).getLocation());
					}
					catch (NullPointerException e) {
						truckNotification(t, Manager.Notification.WAITING);
					}
					
				}
			}
		}
		else {
			try {
				setShortestPath(t, t.getLocation(), getGame().getBoard().getTruckDepot());
			}
			catch (NullPointerException e) {
				truckNotification(t, Manager.Notification.WAITING);
			}
			
		}
		}
		
		
		
	}

	public void setShortestPath(Truck t, Node source, Node destination) {
		
		ArrayList<Node> nodes = new ArrayList<Node>(getGame().getBoard().getNodes());
		double priority = 5;
		ArrayList<Node> visited = new ArrayList<Node>();
		
		for (int i = 0; i < nodes.size(); i++) {
			ArrayList<Object> nodedata = new ArrayList<Object> ();
			nodedata.add(-1);
			nodedata.add(null);
			nodes.get(i).setUserData(nodedata);
		}
		ArrayList<Object> nodedata = new ArrayList<Object> ();
		nodedata.add(0);
		nodedata.add(source);
		nodes.get(nodes.indexOf(source)).setUserData(nodedata);
		
		GriesHeap<Node> frontier = new GriesHeap<Node>();
		frontier.add(source, priority);
		visited.add(source);
		
		ArrayList<Edge> visitedEdges = new ArrayList<Edge>();
		
		while (!frontier.isEmpty()) {
		    Node u = frontier.poll();
		    
		    ArrayList<Edge> exits = new ArrayList<Edge>(u.getExits());
		    
		    	for (Edge e : exits) {
		    		if (!visitedEdges.contains(e)) {
		    		Node v = e.getExits()[0];
		    		if (v == u) {
		    			v = e.getExits()[1];
		    		}
		    		if (!visited.contains(v)) {
		    			int weight = e.length;
		    			ArrayList<Object> data = (ArrayList<Object>) u.getUserData();
		    			int dist = (int) data.get(0) + weight;
		    			ArrayList<Object> datamin = (ArrayList<Object>) v.getUserData();
		    			int vmin = (int) datamin.get(0);
		    		
		    			if (dist < vmin  ||   vmin == -1) {
		    				datamin.set(0, dist);
		    				datamin.set(1, u);
		    				v.setUserData(datamin);
		    				priority = dist;
		    				frontier.add(v,priority);
		    				visited.add(v);
		    			}
		    		}
		    	}
		    	}
		    	visitedEdges.addAll(exits);
		}
		ArrayList<Node> path = new ArrayList<Node> ();
		for (Node node = destination; node != source; node = getPrevious(node, source)) {
			if (path.contains(node)) {
				break;
			}
			path.add(node);
		}
			path.add(source);
			path.remove(null);
			Collections.reverse(path);
			t.setTravelPath(path);
	}

	public Node getPrevious(Node n, Node s) {
		
		try {
			ArrayList<Object> data = (ArrayList<Object>) n.getUserData();
			
			return (Node) data.get(1);
		}
		catch (NullPointerException e) {
			return s;
		}
	}
	
}
