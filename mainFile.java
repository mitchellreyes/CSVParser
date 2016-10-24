import java.awt.*;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class mainFile extends JFrame{

	private static final long serialVersionUID = 1L;
	
	protected JFrame mainWindow;
	public JPanel mainGridLayout;
	
	//menuBar variables
	protected JMenuBar menuBar;
	protected JMenu fileMenu;
	protected JMenuItem helpMenu;
	protected JMenuItem importFileBtn;
	protected JRadioButtonMenuItem comma, semi;
	protected JLabel titleDelim;
	protected ButtonGroup bg;
	protected JMenuItem exportBtn;
	protected JMenu exportWithBtn;
	protected JMenuItem INSTINCTVbtn;
	protected JMenuItem compWith;
	protected JMenuItem addToFavorites;
	protected JMenuItem deleteSeq;
	
	//center grid position variables
	public JPanel gridCenter;
	
	public JTextArea numsLeftTA;
	public JTextArea headersTA;
	public JTextArea numsRightTA;
	public JTextArea statusTA;
	
	public JScrollPane numsLeftSP;
	public JScrollPane headersSP;
	public JScrollPane numsRightSP;
	public JScrollPane statusSP;
	
	//data variables
	List<String> data = new ArrayList<String>();
	public List<List<String>> values = new ArrayList<List<String>>();
	public List<Integer> numCols = new ArrayList<Integer>();
	public List<Integer> userNums = new ArrayList<Integer>();
	public File file;
	public File fileToWrite;
	public String DELIMITER = ";";
	
	public JTextField nameForFaves;
	public JPanel favesPanel;
	public int lineCheck = 0;
	public int lineCross = 0;
	
	public List<favoriteSequence> listOfFaves = new ArrayList<>();
	public List<JRadioButton> listOfButtons = new ArrayList<>();
	public List<JMenuItem> listOfMenuItems = new ArrayList<>();

	
	public static void main(String[] args) {
		new mainFile();
	}
	
	public mainFile()
	{
		System.gc();
		//borders
		TitledBorder title1, title2, title3, title4;
		Border loweredetched1, loweredetched2, loweredetched3, loweredetched4;
		loweredetched1 = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		loweredetched2 = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		loweredetched3 = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		loweredetched4 = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		
		//mainWindow initializers
		mainWindow = new JFrame("CSV Parse");
		mainWindow.setSize(1200, 600);
		//mainWindow.setExtendedState(Frame.MAXIMIZED_BOTH);
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setMinimumSize(new Dimension(1200, 600));
		//JFrame.setDefaultLookAndFeelDecorated(true);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		mainWindow.setLocation(dim.width/2-mainWindow.getSize().width/2, dim.height/2-mainWindow.getSize().height/2);
		
		//mainGridLayout initializers
		mainGridLayout = new JPanel();
		mainGridLayout.setLayout(new GridLayout(2,3));
		
		//Menu Bar initializers
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		importFileBtn = new JMenuItem("Import");
		importFileBtn.addActionListener(new ActionHandler());
		exportBtn = new JMenuItem("Export");
		exportBtn.addActionListener(new ActionHandler());
		exportBtn.setEnabled(false);
		exportWithBtn = new JMenu("Export for...");
		INSTINCTVbtn = new JMenuItem("INSTINCT V");
		INSTINCTVbtn.addActionListener(new ActionHandler());
		helpMenu = new JMenuItem("Help");
		helpMenu.addActionListener(new ActionHandler());
		addToFavorites = new JMenuItem("Add sequence to favorites...");
		addToFavorites.addActionListener(new ActionHandler());
		addToFavorites.setEnabled(false);
		deleteSeq = new JMenuItem("Delete sequences...");
		deleteSeq.addActionListener(new ActionHandler());
		deleteSeq.setEnabled(false);
		
		exportWithBtn.add(INSTINCTVbtn);
		exportWithBtn.setEnabled(false);
		compWith = new JMenuItem("Compare...");
		compWith.addActionListener(new ActionHandler());
		

		fileMenu.add(importFileBtn);
		fileMenu.add(exportBtn);
		fileMenu.add(exportWithBtn);
		fileMenu.add(compWith);
		fileMenu.add(addToFavorites);
		fileMenu.add(deleteSeq);
		fileMenu.add(helpMenu);

		menuBar.add(fileMenu);
		
		importFileBtn.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		compWith.setAccelerator(KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		INSTINCTVbtn.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		exportBtn.setAccelerator(KeyStroke.getKeyStroke('E', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		////////////////////////
		
		//center grid position variables
		gridCenter = new JPanel();
		gridCenter.setLayout(new GridLayout(1, 3));
		
		numsLeftTA = new JTextArea();
		numsLeftSP = new JScrollPane(numsLeftTA);
		numsLeftTA.setEditable(false);
		headersTA = new JTextArea();
		headersSP = new JScrollPane(headersTA);
		headersTA.setEditable(false);
		numsRightTA = new JTextArea("Enter column numbers to delete from file:"
				+ "\n" + "EX: " + '\n' + "1" + '\n' + "7"+ '\n' + "8"+ '\n' + "16"+ '\n' + "17"+ '\n' + "18" + '\n' 
				+ "*Leave this blank if you export with the INSTINCT V option*");
		numsRightSP = new JScrollPane(numsRightTA);
		numsRightTA.setEditable(false);
		
		statusTA = new JTextArea("Import a file to start..." + '\n');
		statusSP = new JScrollPane(statusTA);
		statusTA.setEditable(false);

		title1 = BorderFactory.createTitledBorder(loweredetched1, "Number of Columns in the File: ");
		title2 = BorderFactory.createTitledBorder(loweredetched2, "Headers from the File: ");
		title3 = BorderFactory.createTitledBorder(loweredetched3, "Columns to delete from File: ");
		title4 = BorderFactory.createTitledBorder(loweredetched4, "Program Status");

		
		numsLeftSP.setBorder(title1);
		headersSP.setBorder(title2);
		numsRightSP.setBorder(title3);
		statusSP.setBorder(title4);
		
		new DropTarget(numsLeftTA, new MyDragDropListener());
		new DropTarget(statusTA, new MyDragDropListener());
		new DropTarget(headersTA, new MyDragDropListener());
		
		JScrollBar sp1 = numsLeftSP.getVerticalScrollBar();
	    JScrollBar sp2 = headersSP.getVerticalScrollBar();
	    sp2.setModel(sp1.getModel());

		gridCenter.add(numsLeftSP, "West");
		gridCenter.add(headersSP, "Center");
		gridCenter.add(numsRightSP, "East");
		//////////////////////////////////////
		
		mainGridLayout.add(gridCenter, "Center");
		mainGridLayout.add(statusSP, "South");
		
		mainWindow.getContentPane().add(mainGridLayout, "Center");
		//show the menu bar on the mainWindow
		mainWindow.getContentPane().add(menuBar, "North");
		//show the mainWindow
		mainWindow.setVisible(true);
	}
	
	private void getFile()
	{
		final JFileChooser fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
		fc.setFileFilter(filter);
		fc.setMultiSelectionEnabled(true);
		int returnVal = fc.showOpenDialog(mainFile.this);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			resetProgram();
			file = fc.getSelectedFile();
		}
		else
		{
			return;
		}
	}
	
	
	private File setExportFile()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("./"));
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
		chooser.setSelectedFile(new File(file.getName().replace(".csv", "") + "_parsed"));
		chooser.setFileFilter(filter);
		int returnVal = chooser.showSaveDialog(mainFile.this);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			File fileName = new File(""+ chooser.getSelectedFile() + ".csv");
			return fileName;
		}
		else
		{
			return null;
		}
	}
	
	public void exportData()
	{
		fileToWrite = setExportFile();
		if(fileToWrite != null)
		{
			try(BufferedWriter br = new BufferedWriter(new FileWriter(fileToWrite)))
			{
				statusTA.append("Exporting..." + '\n');
				for(int i = 0; i < values.size(); i++)
				{
					for(int j = 0; j < values.get(i).size(); j++)
					{
						if(!values.get(i).isEmpty())
						{
							br.write(values.get(i).get(j) + DELIMITER + " ");
						}			
					}
					br.write('\n');
				}
				statusTA.append("Export successful to: " + fileToWrite + '\n');
			}catch(FileNotFoundException e)
			{
				
			}catch(IOException e)
			{
				
			}
		}
		
	}
	
	public void importData()
	{
		
		getFile();
		checkDelim();
		List<String> tempList;
		try(BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			statusTA.append("Reading: " + file + '\n');
			while(br.ready())
			{
				data.add(br.readLine());
			}
			for(int i = 0; i < data.size(); i++)
			{
				tempList = new ArrayList<String>(Arrays.asList(data.get(i).split(DELIMITER + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1)));
				values.add(tempList);
			}
			for(int i = 0; i < values.get(0).size(); i++)
			{
				numCols.add(i);
				numsLeftTA.append("[" + (i+1) + "]" + '\n');
				headersTA.append(values.get(0).get(i) + '\n');
			}
			statusTA.append("Read " + data.size() + " lines successfully from file." + '\n');
			numsRightTA.setEditable(true);
			numsRightTA.setText("");
		}catch(FileNotFoundException e)
		{
			statusTA.append("File could not be found." + '\n');
		}catch(IOException e)
		{
			
		}
		exportBtn.setEnabled(true);
		exportWithBtn.setEnabled(true);
		addToFavorites.setEnabled(true);
		deleteSeq.setEnabled(true);
		updateMenuBarWithFavorites();
		
	}
	
	public void checkDelim()
	{
		try(BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			for(int i = 0; i < br.readLine().length(); i++)
			{
				char c = br.readLine().charAt(i);
				if(c == ';')
				{
					DELIMITER = ";";
					break;
				}
				if(c == ',')
				{
					DELIMITER = ",";
					break;
				}
			}

		}
		catch(FileNotFoundException e)
		{

		}catch(IOException e)
		{
			
		}
	}
	
	public void importFromDND()
	{
		List<String> tempList;
		checkDelim();
		try(BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			statusTA.append("Reading: " + file + '\n');
			while(br.ready())
			{	
				data.add(br.readLine());
			}
			for(int i = 0; i < data.size(); i++)
			{
				tempList = new ArrayList<String>(Arrays.asList(data.get(i).split(DELIMITER + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1)));
				values.add(tempList);
			}
			for(int i = 0; i < values.get(0).size(); i++)
			{
				numCols.add(i);
				numsLeftTA.append("[" + (i+1) + "]" + '\n');
				headersTA.append(values.get(0).get(i) + '\n');
			}
			statusTA.append("Read " + data.size() + " lines successfully from file." + '\n');
			numsRightTA.setEditable(true);
			numsRightTA.setText("");
		}catch(FileNotFoundException e)
		{
			statusTA.append("File could not be found." + '\n');
		}catch(IOException e)
		{
			
		}
		exportBtn.setEnabled(true);
		exportWithBtn.setEnabled(true);
		addToFavorites.setEnabled(true);
		deleteSeq.setEnabled(true);
		updateMenuBarWithFavorites();
	}
	
	public void getUserInput()
	{
		String userInput = numsRightTA.getText();
		String[] temp = userInput.split("\\n");
		int num;
		if(temp.length > values.size())
		{
			statusTA.setText("You have entered more numbers than columns" + '\n');
			return;
		}
		for(int i = 0; i < values.size(); i++)
		{
			for(int j = temp.length - 1; j >= 0; j--)
			{
				num = Integer.parseInt(temp[j]) - 1;
				if(num <= values.get(i).size())
				{
					values.get(i).remove(num);
				}
				
			}
		}
	}
	
	public void resetProgram()
	{
		if(file != null)
		{
			file = null;
			headersTA.setText("");
			numsLeftTA.setText("");
        	numsRightTA.setText("Enter column numbers to delete from file:"
        			+ "\n" + "EX: " + '\n' + "1" + '\n' + "7"+ '\n' + "8"+ '\n' + "16"+ '\n' + "17"+ '\n' + "18" + '\n' 
        			+ "*Leave this blank if you export with the INSTINCT V option*");
			data.clear();
			for(int i = 0; i < values.size(); i++)
			{
				values.get(i).clear();
			}
			values.clear();
			numCols.clear();
			userNums.clear();
		}
	}
	
	public class MyDragDropListener implements DropTargetListener {

	    @Override
	    public void drop(DropTargetDropEvent event) {
	        event.acceptDrop(DnDConstants.ACTION_COPY);
	        Transferable transferable = event.getTransferable();
	        DataFlavor[] flavors = transferable.getTransferDataFlavors();
	        for (DataFlavor flavor : flavors) {
	            try {
	                // If the drop items are files
	                if (flavor.isFlavorJavaFileListType()) {
	                	if(file != null)
	                	{
	                		resetProgram();
	                	}
	                    // Get all of the dropped files
						@SuppressWarnings("rawtypes")
						List files = (List)transferable.getTransferData(flavor);

	                    // Loop them through
	                    for (int i = 0; i < files.size(); i++) 
	                    {
	                    	file = (File) files.get(i);
	                    	if(file.getName().contains(".csv"))
	                    	{
	                    		importFromDND();
		        				exportBtn.setEnabled(true);
		        				exportWithBtn.setEnabled(true);
		        				//addToFavorites.setEnabled(true);
	                    	}
	                    	else
	                    	{
	                    		statusTA.append("Cannot open: " + file.getName() + " needs to have ext .csv" + '\n');
	                    	}
	                    }

	                }

	            } catch (Exception e) {
	                // Print out the error stack
	                e.printStackTrace();
	            }
	        }
	        // Inform that the drop is complete
	        event.dropComplete(true);
	    }

	    @Override
	    public void dragEnter(DropTargetDragEvent event) {
	    }

	    @Override
	    public void dragExit(DropTargetEvent event) {
	    }

	    @Override
	    public void dragOver(DropTargetDragEvent event) {
	    }

	    @Override
	    public void dropActionChanged(DropTargetDragEvent event) {
	    }

	}
	
	
	public void printOutEachLine()
	{
		for(int i = 0; i < values.size(); i++)
		{
			for(int j = 0; j < values.get(i).size(); j++)
			{
				System.out.println("HERE IS LINE " + i + " INDEX POS " + j);
				System.out.println(values.get(i).get(j));
			}
		}
		
	}
	
	private void writeFavorites()
	{
		String gotText = numsRightTA.getText();
		String[] nums = gotText.split("\\n");
		File fileForFaves;
		if(gotText.isEmpty())
		{
			statusTA.append("Cannot save an empty pattern...Enter numbers to Columns to delete from File." + '\n');
			return;
		}
		else
		{
			fileForFaves = new File(System.getProperty("java.io.tmpdir") + "csvparserfavorites_v1.4.txt");
			if(file.exists())
			{
				try(FileWriter fw = new FileWriter(fileForFaves, true);
					    BufferedWriter bw = new BufferedWriter(fw);
					    PrintWriter out = new PrintWriter(bw))
					{
					out.println("\"" + nameForFaves.getText());
					for(int i = 0; i < nums.length; i++)
					{
						out.println(", " + nums[i]);
					}
					out.println("\"");
					out.close();
					} catch (IOException e) {

					}
				statusTA.append("\nSequence \n" + gotText + '\n' + "saved." + '\n');
			}
		}
		updateMenuBarWithFavorites();
	}
	
	public class favoriteSequence
	{
		private String name;
		private String sequence;
		
		public favoriteSequence()
		{
			name = "Default";
			sequence = null;
		}
		
		public favoriteSequence(String nameToGive,String someSequence)
		{
			name = nameToGive;
			sequence = someSequence;
		}
		public void setName(String nameToSet)
		{
			name = nameToSet;
		}
		public void setSequence(String seqToSet)
		{
			sequence = seqToSet;
		}
		public String getName()
		{
			return name;
		}
		public String getSequence()
		{
			return sequence;
		}
	}
	
	private void updateMenuBarWithFavorites()
	{
		File favoriteFile = new File(System.getProperty("java.io.tmpdir") + "csvparserfavorites_v1.4.txt");
		String line = "";
		String[] lineData;
		String seqToAdd = "";
		String seqToPrint = "";
		int k = 1;
		if(favoriteFile.exists())
		{
			try
			{
				Scanner scanner = new Scanner(new File("" + favoriteFile));
				scanner.useDelimiter("\"\r\n\"");
				System.out.println("lineCheck: " + lineCheck);
				System.out.println("lineCross: " + lineCheck);
				if(lineCheck > 0)
				{
					while(lineCross != lineCheck)
					{
						line = scanner.next().trim();
						line = line.replace("\n", "").replace("\r", "").replace("\"", "");
						lineCross++;
					}
				}
				
				System.out.println("Line we start at: " + line);
				while(scanner.hasNext())
				{
					favoriteSequence myFaves = new favoriteSequence();
					line = scanner.next().trim();
					line = line.replace("\n", "").replace("\r", "").replace("\"", "");
					
					lineData = line.split(",");
					//eaData.add(lineData);
					
					myFaves.setName(lineData[0]);
					for(int i = 1; i < lineData.length; i++)
					{
						seqToAdd += (lineData[i].trim() + "\n");
					}
					myFaves.setSequence(seqToAdd.trim());
					seqToAdd = "";
					for(; k < lineData.length - 1; k++)
					{
						seqToPrint += (lineData[k].trim() + ", ");
					}
					seqToPrint += (lineData[k].trim());
					JMenuItem faveItem = new JMenuItem(myFaves.getName() + " (" + seqToPrint + ")");
					seqToPrint = "";
					k = 1;
					faveItem.addActionListener(new ActionListener() {
					    public void actionPerformed(ActionEvent e)
					    {
					    	numsRightTA.setText(myFaves.getSequence());
					    	statusTA.append("File ready to be exported." + '\n');
							getUserInput();
							exportData();
					    }
					});
					
					//keeps track of the sequences
					listOfFaves.add(myFaves);
					//keeps track of the menu items
					listOfMenuItems.add(faveItem);
					//adds to the jmenu
					exportWithBtn.add(faveItem);
					lineCheck++;
				}
				scanner.close();
				lineCross = 0;
			}catch (FileNotFoundException e) {
				statusTA.append("An error has occured trying to import favorites." + "\n");
				e.printStackTrace();
			}
		}	
	}
	
	
	public void callDeleteDialog()
	{
		JPanel somePanel = new JPanel();
		somePanel.setLayout(new BoxLayout(somePanel, BoxLayout.LINE_AXIS));
		somePanel.setSize(400, 300);
		//somePanel.add(Box.createHorizontalGlue());
		for(int i = 0; i < listOfFaves.size(); i++)
		{
			JRadioButton someButton = new JRadioButton(listOfFaves.get(i).getName());
			somePanel.add(someButton);
			listOfButtons.add(someButton);
		}
		int option = JOptionPane.showConfirmDialog(null, somePanel, 
				"Delete Sequences... ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	    if(option==0)
	    {
	    	deleteSequencesFromFile();
	    }
	}
	
	public void deleteSequencesFromFile()
	{
		List<String> selectedButtons = new ArrayList<>();
		for(JRadioButton jradiobutton : listOfButtons)
		{
			if(jradiobutton.isSelected())
			{
				selectedButtons.add(jradiobutton.getText().trim());
			}
		}
		File favoriteFile = new File(System.getProperty("java.io.tmpdir") + "csvparserfavorites_v1.4.txt");
		File someNewFile = new File(System.getProperty("java.io.tmpdir") + "temp.txt");
		String line;
		String[] lineData;
		String someString;
		for(int i = 0; i < selectedButtons.size(); i++)
		{
			try
			{
				FileWriter fw = new FileWriter(someNewFile, false);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter writer = new PrintWriter(bw);
				Scanner scanner = new Scanner(favoriteFile);
				scanner.useDelimiter("\"\r\n\"");
				while(scanner.hasNext())
				{
					line = scanner.next().trim();
					line = line.replace("\n", "").replace("\r", "").replace("\"", "");
					lineData = line.split(",");
					if(!lineData[0].trim().equals(selectedButtons.get(i)))
					{
						writer.println("\"" + lineData[0]);
						for(int j = 1; j < lineData.length; j++)
						{
							writer.println("," + lineData[j]);
						}
						writer.println("\"");
					}
					for(int m = 0; m < listOfFaves.size(); m++)
					{
						if(listOfFaves.get(m).getName().equals(selectedButtons.get(i)))
						{
							listOfFaves.remove(m);
							
						}
					}
				}
				writer.close();
				scanner.close();
			}catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(favoriteFile.exists())
			{
				favoriteFile.delete();
			}
			someNewFile.renameTo(favoriteFile);
		}
		
		for(int i = 0; i < listOfMenuItems.size(); i++)
		{
			for(int j = 0; j < selectedButtons.size(); j++)
			{
				someString = listOfMenuItems.get(i).getActionCommand();
				someString = someString.replaceAll("\\(.*?\\) ?", "");
				if(selectedButtons.get(j).equals(someString.trim()))
				{
					statusTA.append(selectedButtons.get(j) + " sequence successfully removed." + '\n');
					exportWithBtn.remove(listOfMenuItems.get(i));
				}
			}
		}
		lineCheck = 0;
	}
	
	public void callDialog()
	{
		favesPanel = new JPanel(new GridLayout(1, 2, 10, 10));
	    favesPanel.setPreferredSize(new Dimension(400, 50));
	    nameForFaves = new JTextField(20);
	    favesPanel.add(nameForFaves);
		int option = JOptionPane.showConfirmDialog(null, favesPanel, 
				"Save Sequence As... ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
	    if(option==0)
	    	writeFavorites();
	}
	
	private class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource() == deleteSeq)
			{
				callDeleteDialog();
			}
			if(e.getSource() == addToFavorites)
			{
				callDialog();
				//updateMenuBarWithFavorites();
			}
			if(e.getSource() == importFileBtn)
			{
				importData();
			}
			if(e.getSource() == exportBtn)
			{
				if(!numsRightTA.getText().isEmpty())
				{
					statusTA.append("File ready to be exported." + '\n');
					getUserInput();
					exportData();
				}
				else
				{
					statusTA.append("Must enter columns to delete before exporting." + '\n');
				}
			}
			if(e.getSource() == INSTINCTVbtn)
			{
				numsRightTA.setText("1" + '\n' + "7" + '\n' + "8" + '\n' + "16" + '\n' + "17" + '\n' + "18" + '\n');
				getUserInput();
				exportData();
			}
			if(e.getSource() == compWith)
			{
				@SuppressWarnings("unused")
				compareWithWindow temp = new compareWithWindow(fileToWrite, statusTA);
			}
			if(e.getSource() == helpMenu)
			{
				//TODO
				statusTA.append("CSV Parser v1.4" + '\n');
			}
		}
	}

}
