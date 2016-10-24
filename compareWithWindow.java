import java.awt.Dimension;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class compareWithWindow extends JFrame{

	private static final long serialVersionUID = 1L;
	public JButton COMPARE;
	public JButton CANCEL;
	public JFrame compareWindow;
	
	public JMenuBar mainBar;
	public JMenu fileMenuItem;
	public JMenuItem importOne;
	
	public JTextField file1field, file2field;
	
	public File[] files;
	
	private boolean areTheyEqual = false;
	public JTextArea temp;
	
	public compareWithWindow(File exportedFile, JTextArea field)
	{
		System.gc();
		
		temp = field;
		
		TitledBorder title1, title2;
		Border loweredetched1, loweredetched2;
		loweredetched1 = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		loweredetched2 = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		
		compareWindow = new JFrame("Compare To...");
		JPanel mainGrid = new JPanel();
		file1field = new JTextField(""+ exportedFile);
		file2field = new JTextField(20);
		title1 = BorderFactory.createTitledBorder(loweredetched1, "File 1: ");
		title2 = BorderFactory.createTitledBorder(loweredetched2, "File 2: ");
		file1field.setBorder(title1);
		file2field.setBorder(title2);
		COMPARE = new JButton("Compare");
		COMPARE.addActionListener(new ActionHandler());
		CANCEL = new JButton("Cancel");
		CANCEL.addActionListener(new ActionHandler());
		
		compareWindow.addWindowListener( new WindowAdapter() {
		    public void windowOpened( WindowEvent e ){
		        file2field.requestFocus();
		    }
		});
		
		mainBar = new JMenuBar();
		fileMenuItem = new JMenu("File");
		importOne = new JMenuItem("Import");
		importOne.addActionListener(new ActionHandler());
		fileMenuItem.add(importOne);
		mainBar.add(fileMenuItem);
		
		compareWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		compareWindow.setSize(600, 300);
		compareWindow.setMinimumSize(new Dimension(300, 300));
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		compareWindow.setLocation(dim.width/2-compareWindow.getSize().width/2, dim.height/2-compareWindow.getSize().height/2);
		
		mainGrid.setLayout(new GridLayout(3, 1));
		file2field.requestFocusInWindow();
		file2field.addActionListener(new ActionHandler());
		mainGrid.add(file1field, "North");
		mainGrid.add(file2field, "South");
		mainGrid.add(COMPARE, "South");
		
		compareWindow.getContentPane().add(mainBar, "North");
		compareWindow.getContentPane().add(mainGrid, "Center");
		
		compareWindow.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel");
	    compareWindow.getRootPane().getActionMap().put("Cancel", new AbstractAction(){
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e)
	            {
	                compareWindow.dispose();
	                System.gc();
	            }
	        });
	    
	    new DropTarget(file1field, new MyDragDropListener(file1field));
	    new DropTarget(file2field, new MyDragDropListener(file2field));
	    
	    importOne.setAccelerator(KeyStroke.getKeyStroke('I', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		compareWindow.setVisible(true);
	}
	
	private void openImportDialog()
	{
		final JFileChooser fc = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
		fc.setFileFilter(filter);
		fc.setMultiSelectionEnabled(true);
		int returnVal = fc.showOpenDialog(compareWithWindow.this);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			files = fc.getSelectedFiles();
			if(files.length > 1)
			{
				file1field.setText(""+ files[0]);
				file2field.setText("" + files[1]);
			}
			else
			{
				file2field.setText(""+ files[0]);
			}
		}
		else
		{
			return;
		}
	}
	
	public boolean compareFiles()
	{
		File file1, file2;
		file1 = new File(file1field.getText().trim());
		file2 = new File(file2field.getText().trim());
		String data1, data2;
		int lineNum = 1;
		try(BufferedReader br = new BufferedReader(new FileReader(file1)))
		{
			try(BufferedReader br2 = new BufferedReader(new FileReader(file2)))
			{
				while(br.ready() && br2.ready())
				{
					data1 = br.readLine();
					data2 = br2.readLine();
					if(!data1.equals(data2))
					{
						temp.append("The two files are different at line " + lineNum + '\n');
						temp.append("\t" + "From file: " + file1 + '\n');
						temp.append("\t\t" + data1 + '\n');
						temp.append('\t' + "From file: " + file2 + '\n');
						temp.append("\t\t" + data2 + '\n');
						return false;
					}
					lineNum++;
				}
				temp.append("The two files are the same" + '\n');
				return true;
			}catch(FileNotFoundException e)
			{
				temp.append("Cannot find the file: " + file2 + '\n');
				return false;
			}catch(IOException e)
			{
				
			}
		}catch(FileNotFoundException e)
		{
			temp.append("Cannot find the file: " + file1 + '\n');
			return false;
		}catch(IOException e)
		{
			
		}
		return false;
	}
	
	public boolean getResultFromComparator()
	{
		return areTheyEqual;
	}
	
	private class ActionHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if(e.getSource() == importOne)
			{
				openImportDialog();
			}
			if(e.getSource() == COMPARE)
			{
				compareFiles();
				compareWindow.dispose();
                System.gc();
			}
			if(e.getSource() == file2field)
			{
				compareFiles();
				compareWindow.dispose();
                System.gc();
			}
		}
	}
	
	
	public class MyDragDropListener implements DropTargetListener {

		JTextField temp;
		public MyDragDropListener(JTextField fileField)
		{
			temp = fileField;
		}
	    @Override
	    public void drop(DropTargetDropEvent event) {
	        event.acceptDrop(DnDConstants.ACTION_COPY);
	        Transferable transferable = event.getTransferable();
	        DataFlavor[] flavors = transferable.getTransferDataFlavors();
	        for (DataFlavor flavor : flavors) {
	            try {
	                // If the drop items are files
	                if (flavor.isFlavorJavaFileListType()) {
						@SuppressWarnings("rawtypes")
						List files = (List)transferable.getTransferData(flavor);
						File fileFromDND = (File) files.get(0);
						temp.setText("" + fileFromDND);
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
}
