/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transformersimulation.model;

import java.util.ArrayList;
import java.util.List;
import prins.simulator.model.Agent;
import prins.simulator.model.Location;
import transformersimulation.TransformerConfig;

/**
 *
 * @author 4simme28
 */
public class Autobot extends Agent {
        
    private List<Location> path;
    private double fitness;
    private double fitnessPenalty = 1;
    private int size;
    
    public Autobot(Location location) {
        super(location);
        path = new ArrayList<>();
    }
    
    public void setFitness(double fitness) {
        fitness = fitness * fitnessPenalty;
        this.fitness = fitness;
    }
    
    public double getFitness() {
        return fitness;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public void generatePath(Planet cybertron) {
        path.clear();
        path.add(location);
        for (int i = 0; i < TransformerConfig.MAX_PATH; i++) {
            Location lastLocation = path.get(path.size() -1);
            List<Location> adjacentLocations = cybertron.findAdjacentLocations(lastLocation);
            int nextInt = TransformerConfig.random.nextInt(adjacentLocations.size());
            path.add(adjacentLocations.get(nextInt));
        }
    }
    
    public void setPath(List<Location> inputPath) {
        path = inputPath;
    }
    
    public List<Location> getPath() {
        return path;
    }
    
    public void act(Planet cybertron) {
        Location nextLocation = location;
        
        // stops at end of list
        if (path.indexOf(location) + 1 < path.size()) {
            nextLocation = path.get(path.indexOf(location)+1);
        }
        
        // stops moving if hitting a obstacle
        if (cybertron.getAgent(nextLocation) instanceof Obstacle) {
            fitnessPenalty = 0.5; // if the autobot hits an obstacle, it's fitness is halved
            cybertron.setAgent(this, location); // Location doesn't change
        } else if (cybertron.getAgent(nextLocation) instanceof Resource) {
            cybertron.setAgent(this, location); // Location doesn't change
        } else {
            // Updates location to next in list
            cybertron.setAgent(null, location);
            cybertron.setAgent(this, nextLocation);
            location = nextLocation;
        }
    }
    
    public Autobot cloneTransformer() {
        Autobot newBot = new Autobot(this.location);
        newBot.setPath(this.getPath());
        newBot.setSize(this.getSize());
        return newBot;
    }
    
}
