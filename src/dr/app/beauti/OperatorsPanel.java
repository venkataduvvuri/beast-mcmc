/*
 * OperatorsPanel.java
 *
 * Copyright (C) 2002-2006 Alexei Drummond and Andrew Rambaut
 *
 * This file is part of BEAST.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership and licensing.
 *
 * BEAST is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 *  BEAST is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BEAST; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package dr.app.beauti;

import org.virion.jam.framework.Exportable;
import org.virion.jam.table.HeaderRenderer;
import org.virion.jam.table.RealNumberCellEditor;
import org.virion.jam.table.TableRenderer;
import org.virion.jam.table.WholeNumberCellEditor;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author			Andrew Rambaut
 * @author			Alexei Drummond
 * @version			$Id: OperatorsPanel.java,v 1.12 2005/07/11 14:07:25 rambaut Exp $
 */
public class OperatorsPanel extends JPanel implements Exportable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3456667023451785854L;
	JScrollPane scrollPane = new JScrollPane();
	JTable operatorTable = null;
	OperatorTableModel operatorTableModel = null;

	JCheckBox autoOptimizeCheck = null;

	public ArrayList operators = new ArrayList();

	class Operator {
		String name;
		String type;
		double tuning;
		int weight;
		String description;
	};

	BeautiFrame frame = null;

	public OperatorsPanel(BeautiFrame parent) {

		this.frame = parent;

		operatorTableModel = new OperatorTableModel();
		operatorTable = new JTable(operatorTableModel);

		operatorTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
		operatorTable.getTableHeader().setReorderingAllowed(false);
		operatorTable.getTableHeader().setDefaultRenderer(
			new HeaderRenderer(SwingConstants.LEFT, new Insets(0, 4, 0, 4)));

//		operatorTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
		operatorTable.getColumnModel().getColumn(0).setCellRenderer(
			new TableRenderer(SwingConstants.LEFT, new Insets(0, 4, 0, 4)));
		operatorTable.getColumnModel().getColumn(0).setPreferredWidth(180);

		operatorTable.getColumnModel().getColumn(1).setCellRenderer(
			new TableRenderer(SwingConstants.LEFT, new Insets(0, 4, 0, 4)));
		operatorTable.getColumnModel().getColumn(1).setPreferredWidth(140);

		operatorTable.getColumnModel().getColumn(2).setCellRenderer(
			new TableRenderer(SwingConstants.LEFT, new Insets(0, 4, 0, 4)));
		operatorTable.getColumnModel().getColumn(2).setCellEditor(
			new RealNumberCellEditor(0, Double.POSITIVE_INFINITY));
		operatorTable.getColumnModel().getColumn(2).setPreferredWidth(50);

		operatorTable.getColumnModel().getColumn(3).setCellRenderer(
			new TableRenderer(SwingConstants.LEFT, new Insets(0, 4, 0, 4)));
		operatorTable.getColumnModel().getColumn(3).setCellEditor(
			new WholeNumberCellEditor(1, Integer.MAX_VALUE));
		operatorTable.getColumnModel().getColumn(3).setPreferredWidth(50);

		operatorTable.getColumnModel().getColumn(4).setCellRenderer(
			new TableRenderer(SwingConstants.LEFT, new Insets(0, 4, 0, 4)));
		operatorTable.getColumnModel().getColumn(4).setPreferredWidth(400);

 		scrollPane = new JScrollPane(operatorTable,
										JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
										JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		scrollPane.setOpaque(false);

		autoOptimizeCheck = new JCheckBox("Auto Optimize - This option will attempt to tune the operators to maximum efficiency. Turn off to tune the operators manually.");
  		autoOptimizeCheck.setOpaque(false);

		JToolBar toolBar1 = new JToolBar();
		toolBar1.setFloatable(false);
		toolBar1.setOpaque(false);
		toolBar1.setLayout(new FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
		toolBar1.add(autoOptimizeCheck);

 		setOpaque(false);
  		setLayout(new BorderLayout(0,0));
		setBorder(new BorderUIResource.EmptyBorderUIResource(new java.awt.Insets(12, 12, 12, 12)));
		add(toolBar1, "North");
		add(scrollPane, "Center");
	}

	public final void operatorsChanged() {
		frame.operatorsChanged();
	}

	public void setOptions(BeautiOptions options) {
		autoOptimizeCheck.setSelected(options.autoOptimize);

		operators = options.selectOperators();

		operatorTableModel.fireTableDataChanged();
	}

	public void getOptions(BeautiOptions options) {

		options.autoOptimize = autoOptimizeCheck.isSelected();

	}

    public JComponent getExportableComponent() {
		return operatorTable;
	}

	class OperatorTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -575580804476182225L;
		String[] columnNames = { "Operates on", "Type", "Tuning", "Weight", "Description" };

		public OperatorTableModel() {
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return operators.size();
		}

		public Object getValueAt(int row, int col) {
			BeastGenerator.Operator op = (BeastGenerator.Operator)operators.get(row);
			switch (col) {
				case 0: return op.name;
				case 1: return op.type;
				case 2: if (op.isTunable()) {
					return new Double(op.tuning);
				} else {
					return "n/a";
				}
				case 3: return new Integer((int)op.weight);
				case 4: return op.getDescription();
			}
			return null;
		}

		public void setValueAt(Object aValue, int row, int col) {
			BeastGenerator.Operator op = (BeastGenerator.Operator)operators.get(row);
			switch (col) {
				case 2: op.tuning = ((Double)aValue).doubleValue(); break;
				case 3: op.weight = ((Integer)aValue).intValue(); break;
			}

			operatorsChanged();
		}

		public String getColumnName(int column) {
			return columnNames[column];
		}

		public Class getColumnClass(int c) {return getValueAt(0, c).getClass();}

        public boolean isCellEditable(int row, int col) {
			if (col == 2) {
				BeastGenerator.Operator op = (BeastGenerator.Operator)operators.get(row);
 				if (!op.isTunable()) return false;
              	return true;
            } else if (col == 3) {
            	return true;
            }
 			return false;
        }

		public String toString() {
			StringBuffer buffer = new StringBuffer();

			buffer.append(getColumnName(0));
			for (int j = 1; j < getColumnCount(); j++) {
				buffer.append("\t");
				buffer.append(getColumnName(j));
			}
			buffer.append("\n");

			for (int i = 0; i < getRowCount(); i++) {
				buffer.append(getValueAt(i, 0));
				for (int j = 1; j < getColumnCount(); j++) {
					buffer.append("\t");
					buffer.append(getValueAt(i, j));
				}
				buffer.append("\n");
			}

			return buffer.toString();
		}
	};
}
