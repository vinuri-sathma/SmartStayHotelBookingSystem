
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ViewRoomsFrame extends JFrame {

    public ViewRoomsFrame(RoomManager roomManager) {

        setTitle("SmartStay - View Rooms");
        setSize(750, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columns = {
                "Room ID", "Room Type", "Bed Type",
                "Daily Rate (LKR)", "Status"
        };

        DefaultTableModel tableModel =
                new DefaultTableModel(columns, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

        for (Room room : roomManager.getAllRooms()) {
            Object[] row = {
                    room.getRoomId(),
                    room.getRoomType(),
                    room.getBedType(),
                    room.getDailyRate(),
                    room.getStatus()
            };

            tableModel.addRow(row);
        }

        JTable roomTable = new JTable(tableModel);
        roomTable.setRowHeight(28);

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        add(scrollPane, BorderLayout.CENTER);
    }
}
