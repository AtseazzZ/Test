package demo;

import javax.swing.*;
import java.awt.*;

public class Menus {
    public static void main(String[] args) {
        // 创建主窗体
        JFrame frame = new JFrame("Menu");
        frame.setSize(500, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // 居中

        // 设置全局字体
        Font cnFont = new Font("Microsoft YaHei", Font.PLAIN, 14);
        UIManager.put("Menu.font", cnFont);
        UIManager.put("MenuItem.font", cnFont);
        UIManager.put("CheckBoxMenuItem.font", cnFont);

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();

        // 创建菜单
        JMenu menu1 = new JMenu("File");
        JMenu menu2 = new JMenu("Format");
        JMenu menu3 = new JMenu("Help");

        // 添加到菜单栏
        menuBar.add(menu1);
        menuBar.add(menu2);
        menuBar.add(menu3);

        // 添加子菜单/菜单项（中文）
        JMenuItem chineseItem = new JMenuItem("中文");
        menu2.add(chineseItem);

        JMenu baseMenu = new JMenu("进制");
        menu2.add(baseMenu);

        JCheckBoxMenuItem bin = new JCheckBoxMenuItem("二进制");
        JCheckBoxMenuItem oct = new JCheckBoxMenuItem("八进制");
        JCheckBoxMenuItem dec = new JCheckBoxMenuItem("十进制");
        baseMenu.add(bin);
        baseMenu.add(oct);
        baseMenu.add(dec);

        // 放入窗体
        frame.setJMenuBar(menuBar);

        frame.setVisible(true);
    }
}
