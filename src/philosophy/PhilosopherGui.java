package philosophy;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PhilosopherGui {
	
	private JLabel status;
	private JLabel leftFork; 
	private JLabel rightFork;
	private JLabel cup;
	private JButton hungryB;
	private JButton satisB;
	private JLabel drinkingStatus;
	
	private boolean disabled;
	
	public PhilosopherGui(boolean disabled) {
		this.disabled = disabled;
		if(disabled)
			return;
		
		JFrame guiFrame = new JFrame();
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle("Philosopher");
        guiFrame.setSize(300,150);
        
        JLabel message = new JLabel("I am a Philosopher.");
        status = new JLabel("I am thinking");
        JPanel messagePanel = new JPanel();
        
        messagePanel.add(message);
        
        JPanel foodStatusPanel = new JPanel();
        leftFork = new JLabel("fork");
        rightFork = new JLabel("");
        leftFork.setBorder(BorderFactory.createLineBorder(Color.black));
        rightFork.setBorder(BorderFactory.createLineBorder(Color.black));
        leftFork.setSize(30,20);
        rightFork.setSize(30,20);
        

        
        foodStatusPanel.add(leftFork);
        foodStatusPanel.add(status);

        foodStatusPanel.add(rightFork);
        
        drinkingStatus = new JLabel("");
        JPanel drinkStatusPanel = new JPanel();
        cup = new JLabel("");
        cup.setBorder(BorderFactory.createLineBorder(Color.black));
        cup.setSize(20,20);
        drinkStatusPanel.add(cup);
        drinkStatusPanel.add(drinkingStatus);
        
        
        
        hungryB = new JButton("Become Hungry");
        satisB = new JButton("Be Satisfied");
        hungryB.addActionListener(a -> {Philosopher.hungerFlag = true;});
        satisB.addActionListener(a -> {Philosopher.satisfactionFlag = true;});
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(hungryB);
        buttonPanel.add(satisB);
        
        JPanel infoPanel = new JPanel();
        

        infoPanel.setLayout(new GridLayout(2,1));
        infoPanel.add(foodStatusPanel);
        infoPanel.add(drinkStatusPanel);
        
        guiFrame.add(messagePanel, BorderLayout.NORTH);
        
        guiFrame.add(buttonPanel, BorderLayout.SOUTH);
        guiFrame.add(infoPanel, BorderLayout.CENTER);
        
        guiFrame.setVisible(true);
        update();
	}
	
	public void update(){
		if(disabled)
			return;
		
		updateHungerState();
		updateThirstState();
		updateForks();
		updateCup();
	}
	
	private void updateCup() {
		// TODO Auto-generated method stub
		if(disabled)
			return;
		if(Philosopher.hasCup){
			cup.setText("cup");
		} else {
			cup.setText("  ");
		}
		
	}

	public void updateHungerState() {
		if(disabled)
			return;
		
		status.setText(Philosopher.hungerState.toString());
		if(Philosopher.hungerState.equals(HungerState.THINKING)){
			hungryB.setEnabled(true);
			satisB.setEnabled(false);
		} else if (Philosopher.hungerState.equals(HungerState.HUNGRY)){
			hungryB.setEnabled(false);
			satisB.setEnabled(false);
		} else {
			hungryB.setEnabled(false);
			satisB.setEnabled(true);
		}
	}
	
	public void updateThirstState() {
		if(disabled)
			return;
		
		drinkingStatus.setText(Philosopher.thirstState.toString());
	}
	
	public void updateForks() {
		if(disabled)
			return;
		
		if(Philosopher.leftHand.exists){
			leftFork.setText("fork");
		} else {
			leftFork.setText("   ");
		}
		
		if(Philosopher.rightHand.exists){
			rightFork.setText("fork");
		} else {
			rightFork.setText("   ");
		}
	}
}
