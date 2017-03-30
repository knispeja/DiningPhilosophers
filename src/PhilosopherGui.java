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
        
        JPanel statusPanel = new JPanel();
        leftFork = new JLabel("fork");
        rightFork = new JLabel("");
        leftFork.setBorder(BorderFactory.createLineBorder(Color.black));
        rightFork.setBorder(BorderFactory.createLineBorder(Color.black));
        leftFork.setSize(30,20);
        rightFork.setSize(30,20);
        
        statusPanel.add(leftFork);
        statusPanel.add(status);
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
        updateGUI();
	}
	
	
	public void updateGUI(){
		
		if(disabled)
			return;
		
		if(Philosopher.state.equals(Philosopher.State.THINKING)){
			status.setText("I am thinking");
			hungryB.setEnabled(true);
			satisB.setEnabled(false);
		} else if (Philosopher.state.equals(Philosopher.State.HUNGRY)){
			status.setText("I am hungry");
			hungryB.setEnabled(false);
			satisB.setEnabled(false);
		} else {
			status.setText("I am eating");
			hungryB.setEnabled(false);
			satisB.setEnabled(true);
		}
		
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
