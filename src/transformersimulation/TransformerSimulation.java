/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transformersimulation;

import java.awt.Color;
import java.util.ArrayList;
import prins.simulator.Simulator;
import prins.simulator.view.Gui;
import transformersimulation.model.Cybertron;

/**
 *
 * @author 4simme28
 */
public class TransformerSimulation extends Simulator {

    private Cybertron cybertron;
    private Gui gui;
    
    public TransformerSimulation() {
        cybertron = new Cybertron();
        gui = new Gui(cybertron);
        
        populate();
    }
    
    private void populate() {

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

    }

}
