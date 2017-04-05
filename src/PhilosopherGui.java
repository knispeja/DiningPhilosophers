import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PhilosopherGui {
	
	private JLabel status;
	private JLabel leftFork; 
	private JLabel rightFork;
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
        
        drinkingStatus = new JLabel("");
        messagePanel.add(message);
        
        JPanel statusPanel = new JPanel();
        leftFork = new JLabel("fork");
        rightFork = new JLabel("");
        leftFork.setBorder(BorderFactory.createLineBorder(Color.black));
        rightFork.setBorder(BorderFactory.createLineBorder(Color.black));
        leftFork.setSize(30,20);
        rightFork.setSize(30,20);
        
        statusPanel.add(leftFork);
        statusPanel.add(status);
        statusPanel.add(drinkingStatus);
        statusPanel.add(rightFork);
        
        hungryB = new JButton("Become Hungry");
        satisB = new JButton("Be Satisfied");
        
        hungryB.addActionListener(a -> {Philosopher.hungerFlag = true;});
        satisB.addActionListener(a -> {Philosopher.satisfactionFlag = true;});
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(hungryB);
        buttonPanel.add(satisB);
        
        guiFrame.add(messagePanel, BorderLayout.NORTH);
        guiFrame.add(buttonPanel, BorderLayout.SOUTH);
        guiFrame.add(statusPanel, BorderLayout.CENTER);
        
        guiFrame.setVisible(true);
        update();
	}
	
	public void update(){
		if(disabled)
			return;
		
		updateHungerState();
		updateThirstState();
		updateForks();
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
