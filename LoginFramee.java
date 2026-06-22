package demo;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class LoginFramee extends JFrame implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    public LoginFramee() {
        setTitle("登陆");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel userLabel = new JLabel("用户名：");
        JLabel passLabel = new JLabel("密  码：");

        usernameField = new JTextField("zrw", 15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("确定");

        loginButton.addActionListener(this);

        panel.add(userLabel);
        panel.add(usernameField);
        panel.add(passLabel);
        panel.add(passwordField);
        panel.add(new JLabel(""));
        panel.add(loginButton);
        add(panel);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if ("zrw".equals(username) && "123456".equals(password)) {
            JOptionPane.showMessageDialog(this, "登录成功！");
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误！");
        }
    }

    public static void main(String[] args) {
        new LoginFramee().setVisible(true);
    }
}
