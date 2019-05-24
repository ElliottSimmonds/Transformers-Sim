/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transformersimulation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import prins.simulator.Simulator;
import prins.simulator.model.Location;
import prins.simulator.view.Gui;
import transformersimulation.model.Autobot;
import transformersimulation.model.Planet;
import transformersimulation.model.Resource;
import transformersimulation.model.Obstacle;

/**
 *
 * @author 4simme28
 */
public class TransformerSimulation extends Simulator {

    private Planet cybertron;
    private Gui gui;
    private List<Autobot> autobots;
    private List<Resource> resources;
    Obstacle obstacles;
    private int counter;
    
    public TransformerSimulation() {
        cybertron = new Planet();
        gui = new Gui(cybertron);
        gui.registerAgentColors(Autobot.class, Color.blue);
        gui.registerAgentColors(Resource.class, Color.orange);
        gui.registerAgentColors(Obstacle.class, Color.red);
        
        populate();
    }
    
    private void populate() {
        loadArea();
       
        autobots = new ArrayList<>();
        for (int i=0; i < TransformerConfig.MAX_TRANSFORMERS; i++) {
            Autobot autobot = new Autobot(new Location(2,15));
            int randSize = TransformerConfig.random.nextInt(5) + 1;  // 1 is added so it ranges from 1-5 rather than 0-4
            autobot.setSize(randSize);
            autobot.generatePath(cybertron);
            autobots.add(autobot);
        }
    }
    
    private void loadArea() {
        // generates obstacles and resources
		
	resources = new ArrayList<>();

	Resource resource = new Resource(new Location(23,22));
	resources.add(resource);
	cybertron.setAgent(resource, resources.getLocation());

        Resource resource = new Resource(new Location(15,3));
		resources.add(resource);
        cybertron.setAgent(resources, resources.getLocation());

        for (int i=0; i < 7; i++) {
            obstacles = new Obstacle(new Location(10,i));
            cybertron.setAgent(obstacles, obstacles.getLocation());
        }
        
        for (int i=10; i < 14; i++) {
            obstacles = new Obstacle(new Location(i,7));
            cybertron.setAgent(obstacles, obstacles.getLocation());
        }
        
        for (int i=20; i < 30; i++) {
            obstacles = new Obstacle(new Location(15,i));
            cybertron.setAgent(obstacles, obstacles.getLocation());
        }
    }
    
    @Override
    protected void prepare() {
        gui.addPropertyChangeListener(this);
    }

    @Override
    protected void render() {
        gui.setStep(step);
        gui.render();
    }

    @Override
    protected void reset() {
        cybertron.clear();
    }

    @Override
    protected void update() {
        if (step % TransformerConfig.MAX_PATH == 0) {
            //work out fitness
            autobots.forEach(autobot -> {
            
            int autobotX = autobot.getLocation().getX();
            int autobotY = autobot.getLocation().getY();
	    double fitness = 0;

	    resources.forEach(resource -> {
		int targetX = resource.getLocation().getX();
		int targetY = resource.getLocation().getY();

		double differenceX = Math.abs(targetX - autobotX);
		double differenceY = Math.abs(targetY - autobotY);
		double difference = Math.sqrt(Math.pow(differenceX,2) + Math.pow(differenceY,2));
		double newFitness = 1 / difference;

		if (newFitness > fitness) {
			fitness = newFitness;
		};
	    });
            
            autobot.setFitness(fitness);
            
            });
            selection();
        }
        autobots.forEach(autobot -> {
            if (step != 0 && step % autobot.getSize() == 0) {
                autobot.act(cybertron);
            }
        });
    }

    private void selection() {
        List<Autobot> genePool = generateGenePool();
        autobots.clear();
        cybertron.clear();
        
        loadArea();
        
        for (int i=0; i < TransformerConfig.MAX_TRANSFORMERS; i++) {
            Autobot parent = genePool.get(TransformerConfig.random.nextInt(genePool.size()));
            
            Autobot baby = reproduce(parent);
            autobots.add(baby);
            cybertron.setAgent(baby, baby.getLocation());
        }
        counter = counter + 1;
        System.out.println("Generation "+counter);
        step = 0;
    }

    private Autobot reproduce(Autobot parent) {
        Autobot baby = new Autobot(new Location(2,15));
        
        List<Location> parentPath = parent.getPath();
        List<Location> babyPath = new ArrayList<Location>();
        
        baby.setSize(parent.getSize()); // inherits size from parent
        
        babyPath.add(parentPath.get(0));
        for (int i=1; i < parentPath.size(); i++) {
            int parentDirectionX = parentPath.get(i).getX() - parentPath.get(i-1).getX();
            int parentDirectionY = parentPath.get(i).getY() - parentPath.get(i-1).getY();
            int babyX;
            int babyY;
            
            // path mutation. can occur on each path step.
            int randInt = TransformerConfig.random.nextInt(100);
            if (randInt < 5) {
                // moves in random direction
                int randIntX = (int)Math.round((Math.random() * 2) -1);
                int randIntY = (int)Math.round((Math.random() * 2) -1);
                babyX = babyPath.get(babyPath.size()-1).getX() + randIntX;
                babyY = babyPath.get(babyPath.size()-1).getY() + randIntY;
                
            } else {
                // moves in the same direction as parent
                babyX = babyPath.get(babyPath.size()-1).getX() + parentDirectionX;
                babyY = babyPath.get(babyPath.size()-1).getY() + parentDirectionY;
            }

            // prevents the path going out of bounds
            if (babyX >= 30) {
                babyX = 29;
            }
            if (babyX < 0) {
                babyX = 0;
            }
            if (babyY >= 30) {
                babyY = 29;
            }
            if (babyY < 0) {
                babyY = 0;
            }
			
            babyPath.add(new Location(babyX, babyY));
            
        }
        baby.setPath(babyPath);
        return baby;
    }
    
    private List<Autobot> generateGenePool() {
        List<Autobot> genePool = new ArrayList<Autobot>();
        autobots.forEach(autobot -> {
            int numAutobots = (int)((autobot.getFitness()*10) * TransformerConfig.MAX_TRANSFORMERS);
            for (int i=0; i < numAutobots; i++) {
                genePool.add(autobot.cloneTransformer());
            }
        } );
        return genePool;
    }
    
}
