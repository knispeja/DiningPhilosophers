package philosophy;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class PhilosopherGui {
	
	private static final String RAND_BTN_TEXT_WHILE_ON = "Random Mode Off";
	private static final String RAND_BTN_TEXT_WHILE_OFF = "Random Mode On";
	
	private static final int GRID_HGAP = 2;
	private static final int GRID_VGAP = 5;
	
	private static final int EMPTY_BORDER_SIZE_TOP = 20;
	private static final int EMPTY_BORDER_SIZE_BOT = 9;
	
	private JLabel status;
	private JLabel leftFork; 
	private JLabel rightFork;
	private JLabel cup;
	private JButton thirstyB;
	private JButton hungryB;
	private JButton satisB;
	private JButton sleepB;
	private JButton wakeB;
	private JButton randB;
	private JButton stopPlayB;
	
	private JButton playLeftB;
	private JButton playRightB;
	
	private JLabel drinkingStatus;
	private JLabel playStatus;
	
	private boolean disabled;
	
	public PhilosopherGui(boolean disabled) {
		this.disabled = disabled;
		if(disabled)
			return;
		
		JFrame guiFrame = new JFrame();
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle("Philosopher");
        guiFrame.setSize(300,150);
        
        status = new JLabel("I am thinking");
        
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
        
        JPanel playStatusPanel = new JPanel();
        playStatus = new JLabel("");
        playStatusPanel.add(playStatus);
        
        
        
        hungryB = new JButton("Become Hungry");
        thirstyB = new JButton("Become Thirsty");
        satisB = new JButton("Be Satisfied");
        sleepB = new JButton("Sleep Forever!");
        wakeB = new JButton("Wake Up!");
        randB = new JButton(RAND_BTN_TEXT_WHILE_ON);
        hungryB.addActionListener(a -> {Philosopher.hungerFlag = true;});
        thirstyB.addActionListener(a -> {Philosopher.thirstFlag = true;});
        satisB.addActionListener(a -> {Philosopher.satisfactionFlag = true;});
        sleepB.addActionListener(a -> {Philosopher.sleepFlag = true;});
        wakeB.addActionListener(a -> {Philosopher.wakenFlag = true;});
        randB.addActionListener(a -> {
        	Philosopher.randomMode = !Philosopher.randomMode;
        	if(Philosopher.randomMode) randB.setText(RAND_BTN_TEXT_WHILE_ON);
        	else randB.setText(RAND_BTN_TEXT_WHILE_OFF);
        });
        
        playLeftB = new JButton("Play Game (Left)");
        playLeftB.addActionListener(a -> {Philosopher.playLeftFlag = true;});
        playRightB = new JButton("Play Game (Right)");
        playRightB.addActionListener(a -> {Philosopher.playRightFlag = true;});
        stopPlayB = new JButton("Stop Playing");
        stopPlayB.addActionListener(a -> {Philosopher.stopPlayFlag = true;});        
        
        
        
        JPanel buttonPanel = new JPanel();
        
        
        
        buttonPanel.add(playLeftB);
        buttonPanel.add(stopPlayB);
        buttonPanel.add(playRightB);
        buttonPanel.add(hungryB);
        buttonPanel.add(thirstyB);
        buttonPanel.add(satisB);
        buttonPanel.add(sleepB);
        buttonPanel.add(wakeB);
        buttonPanel.add(randB);
        buttonPanel.setLayout(new GridLayout(3, 3, GRID_HGAP, GRID_VGAP));
        buttonPanel.setBorder(new EmptyBorder(EMPTY_BORDER_SIZE_BOT, EMPTY_BORDER_SIZE_BOT, EMPTY_BORDER_SIZE_BOT, EMPTY_BORDER_SIZE_BOT));
        
        
        JPanel infoPanel = new JPanel();
        
        infoPanel.setLayout(new GridLayout(3, 1));
        infoPanel.add(foodStatusPanel);
        infoPanel.add(drinkStatusPanel);
        infoPanel.add(playStatusPanel);
        infoPanel.setBorder(new EmptyBorder(EMPTY_BORDER_SIZE_TOP, EMPTY_BORDER_SIZE_TOP, EMPTY_BORDER_SIZE_TOP, EMPTY_BORDER_SIZE_TOP));
        

        guiFrame.add(infoPanel, BorderLayout.NORTH);
        guiFrame.add(buttonPanel, BorderLayout.SOUTH);
        

        
        
        
        guiFrame.pack();
        guiFrame.setVisible(true);
        update();
	}
	
	public void update(){
		if(disabled)
			return;
		
		updateHungerState();
		updateThirstState();
		updatePlayState();
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
	
	public void updatePlayState() {
		if(disabled)
			return;
		
		playStatus.setText(Philosopher.playState.toString());
		if(Philosopher.playState.equals(PlayState.PLAY_LEFT) || Philosopher.playState.equals(PlayState.PLAY_RIGHT)) {
			stopPlayB.setEnabled(true);
			playLeftB.setEnabled(false);
			playRightB.setEnabled(false);
		} 
		else if(Philosopher.playState.equals(PlayState.WANT_PLAY_LEFT) || Philosopher.playState.equals(PlayState.WANT_PLAY_RIGHT)) {
			stopPlayB.setEnabled(false);
			playLeftB.setEnabled(false);
			playRightB.setEnabled(false);
		} 
		else {
			stopPlayB.setEnabled(false);
			playLeftB.setEnabled(true);
			playRightB.setEnabled(true);
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
		
		if(Philosopher.thirstState.equals(ThirstState.DRINKING)) {
			thirstyB.setEnabled(false);
			sleepB.setEnabled(true);
			wakeB.setEnabled(false);
		} else if(Philosopher.sleepState.equals(SleepState.SLEEPING)) {
			thirstyB.setEnabled(false);
			sleepB.setEnabled(false);
			wakeB.setEnabled(true);
			drinkingStatus.setText(SleepState.SLEEPING.toString());
		} else if(Philosopher.thirstState.equals(ThirstState.THINKING)) {
			thirstyB.setEnabled(true);
			sleepB.setEnabled(true);
			wakeB.setEnabled(false);
		} else { // Thirsty
			thirstyB.setEnabled(false);
			sleepB.setEnabled(true);
			wakeB.setEnabled(false);
		}
	}
	
	public void updateForks() {
		if(disabled)
			return;
		
		if(Philosopher.leftFork.exists){
			leftFork.setText("fork");
		} else {
			leftFork.setText("   ");
		}
		
		if(Philosopher.rightFork.exists){
			rightFork.setText("fork");
		} else {
			rightFork.setText("   ");
		}
	}
}
