package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminInterfaz extends JFrame {
    private final String username;

    public AdminInterfaz(String username) {
        this.username = username;
        setTitle("Panel de Administración");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton cambiarContraseñaButton = new JButton("Cambiar Contraseña");
        cambiarContraseñaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cambiarContraseña();
            }
        });

        JButton borrarUsuarioButton = new JButton("Borrar Usuario");
        borrarUsuarioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                borrarUsuario();
            }
        });

        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(cambiarContraseñaButton);
        panel.add(borrarUsuarioButton);

        add(panel);
    }

    private void cambiarContraseña() {
        String nuevaContraseña = JOptionPane.showInputDialog(this, "Ingresa tu nueva contraseña:");

        if (nuevaContraseña != null && !nuevaContraseña.isEmpty()) {
            MongoDBConnection mongoDBConnection = null;
            try {
                mongoDBConnection = MongoDBConnection.getInstance();
                MongoCollection<Document> userCollection = mongoDBConnection.getUserCollection();

                // Actualizar la contraseña en la base de datos
                Bson filter = Filters.eq("username", username);
                Bson update = new Document("$set", new Document("password", nuevaContraseña));
                userCollection.updateOne(filter, update);

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Contraseña cambiada exitosamente.");
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error al intentar cambiar la contraseña.", "Error", JOptionPane.ERROR_MESSAGE);
                });
            } finally {
                if (mongoDBConnection != null) {
                    mongoDBConnection.closeConnection();
                }
            }
        } else {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "La contraseña no puede estar vacía. Inténtalo de nuevo.", "Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    private void borrarUsuario() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas borrar tu cuenta?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            MongoDBConnection mongoDBConnection = null;
            try {
                mongoDBConnection = MongoDBConnection.getInstance();
                MongoCollection<Document> userCollection = mongoDBConnection.getUserCollection();

                // Borrar el usuario de la base de datos
                Bson filter = Filters.eq("username", username);
                userCollection.deleteOne(filter);

                // Cerrar la ventana de administración después de borrar el usuario
                dispose();

                // Mostrar un mensaje y cerrar la aplicación (puedes ajustar esto según tu flujo)
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Usuario borrado exitosamente. La aplicación se cerrará.");
                    System.exit(0);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error al intentar borrar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                });
            } finally {
                if (mongoDBConnection != null) {
                    mongoDBConnection.closeConnection();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Ejemplo de uso: supongamos que el usuario "john" ha iniciado sesión
            new AdminInterfaz("john").setVisible(true);
        });
    }
}
