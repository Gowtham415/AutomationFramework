package com.textura.framework.automationcontrollers;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class TextInputOptionPane extends JOptionPane {

	private static final long serialVersionUID = -4171048795445505675L;

	public static String showInputDialog(JFrame frame, final String message, final int row, final int col) {
		String data = null;
		class GetData extends JDialog implements ActionListener {

			private static final long serialVersionUID = 6048300366402861735L;
			// JTextArea ta = new JTextArea(35, 10);
			JTextArea ta = new JTextArea(row, col);
			JButton btnOK = new JButton("   OK   ");
			JButton btnCancel = new JButton("Cancel");
			String str = null;

			public GetData() {
				setModal(true);
				getContentPane().setLayout(new BorderLayout());
				setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				setLocationRelativeTo(null);
				Point p = this.getLocation();
				setLocation(p.x - 110, p.y - 410);
				getContentPane().add(new JLabel(message), BorderLayout.NORTH);
				getContentPane().add(ta, BorderLayout.CENTER);
				JPanel jp = new JPanel();
				btnOK.addActionListener(this);
				btnCancel.addActionListener(this);
				jp.add(btnOK);
				jp.add(btnCancel);
				getContentPane().add(jp, BorderLayout.SOUTH);
				pack();
				setVisible(true);
			}

			public void actionPerformed(ActionEvent ae) {
				if (ae.getSource() == btnOK)
					str = ta.getText();
				dispose();
			}

			public String getData() {
				return str;
			}
		}
		data = new GetData().getData();
		return data;
	}
}