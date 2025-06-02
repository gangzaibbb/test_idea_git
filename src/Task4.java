import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Task4 extends JFrame {
    // 资源数量
    private final int[] available = {17, 5, 20};

    // 最大需求矩阵
    private final int[][] max = {
            {5, 5, 9},
            {5, 3, 6},
            {4, 0, 11},
            {4, 2, 5},
            {4, 2, 4}
    };

    // 分配矩阵
    private final int[][] allocation = {
            {2, 1, 2},
            {4, 0, 2},
            {4, 0, 5},
            {2, 0, 4},
            {3, 1, 4}
    };

    // 需求矩阵
    private final int[][] need;

    // 进程数量
    private final int processCount = 5;
    // 资源种类
    private final int resourceCount = 3;

    private JTextArea outputArea;
    private JTextField aField, bField, cField;

    public Task4() {
        // 初始化需求矩阵
        need = new int[processCount][resourceCount];
        for (int i = 0; i < processCount; i++) {
            for (int j = 0; j < resourceCount; j++) {
                need[i][j] = max[i][j] - allocation[i][j];
            }
        }

        // 计算当前可用资源
        calculateAvailable();

        setTitle("银行家算法");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 创建输入面板
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("P2进程请求资源:"));
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel("A资源数量:"));
        aField = new JTextField();
        inputPanel.add(aField);
        inputPanel.add(new JLabel("B资源数量:"));
        bField = new JTextField();
        inputPanel.add(bField);
        inputPanel.add(new JLabel("C资源数量:"));
        cField = new JTextField();
        inputPanel.add(cField);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        JButton checkSafetyBtn = new JButton("检查安全状态");
        JButton requestBtn = new JButton("处理请求");
        buttonPanel.add(checkSafetyBtn);
        buttonPanel.add(requestBtn);

        // 创建输出区域
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // 添加组件到主窗口
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // 添加事件监听器
        checkSafetyBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkSafety();
            }
        });

        requestBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processRequest();
            }
        });
    }

    // 计算当前可用资源
    private void calculateAvailable() {
        int[] used = new int[resourceCount];
        for (int i = 0; i < processCount; i++) {
            for (int j = 0; j < resourceCount; j++) {
                used[j] += allocation[i][j];
            }
        }

        for (int j = 0; j < resourceCount; j++) {
            available[j] -= used[j];
        }
    }

    // 检查系统是否处于安全状态
    private void checkSafety() {
        outputArea.setText("");
        outputArea.append("开始检查系统安全状态...\n");

        // 拷贝工作向量
        int[] work = new int[resourceCount];
        System.arraycopy(available, 0, work, 0, resourceCount);

        // 初始化finish数组
        boolean[] finish = new boolean[processCount];

        // 安全序列
        int[] safeSequence = new int[processCount];
        int count = 0;

        while (count < processCount) {
            boolean found = false;
            for (int i = 0; i < processCount; i++) {
                if (!finish[i] && checkNeedLessThanWork(i, work)) {
                    // 分配资源给进程i
                    for (int j = 0; j < resourceCount; j++) {
                        work[j] += allocation[i][j];
                    }

                    safeSequence[count] = i;
                    count++;
                    finish[i] = true;
                    found = true;

                    outputArea.append("找到可执行进程: P" + (i + 1) + "\n");
                    outputArea.append("工作向量变为: A=" + work[0] + " B=" + work[1] + " C=" + work[2] + "\n");
                }
            }

            if (!found) {
                break;
            }
        }

        if (count == processCount) {
            outputArea.append("\n系统处于安全状态！安全序列为: ");
            for (int i = 0; i < processCount; i++) {
                outputArea.append("P" + (safeSequence[i] + 1));
                if (i != processCount - 1) {
                    outputArea.append(" -> ");
                }
            }
            outputArea.append("\n");
        } else {
            outputArea.append("\n系统处于不安全状态！\n");
        }
    }

    // 检查进程的need是否小于等于work
    private boolean checkNeedLessThanWork(int process, int[] work) {
        for (int j = 0; j < resourceCount; j++) {
            if (need[process][j] > work[j]) {
                return false;
            }
        }
        return true;
    }

    // 处理资源请求
    private void processRequest() {
        outputArea.setText("");

        try {
            int a = Integer.parseInt(aField.getText());
            int b = Integer.parseInt(bField.getText());
            int c = Integer.parseInt(cField.getText());

            if (a < 0 || b < 0 || c < 0) {
                outputArea.append("错误: 资源请求量不能为负数！\n");
                return;
            }

            int[] request = {a, b, c};
            int process = 1; // P2的索引是1

            outputArea.append("处理P2的资源请求: A=" + a + " B=" + b + " C=" + c + "\n");

            // 第一步: 检查请求是否小于等于need
            for (int j = 0; j < resourceCount; j++) {
                if (request[j] > need[process][j]) {
                    outputArea.append("错误: 请求的资源超过了进程P2的最大需求！\n");
                    return;
                }
            }

            // 第二步: 检查请求是否小于等于available
            for (int j = 0; j < resourceCount; j++) {
                if (request[j] > available[j]) {
                    outputArea.append("错误: 请求的资源超过了系统可用资源！\n");
                    return;
                }
            }

            // 尝试分配资源
            outputArea.append("尝试分配资源...\n");

            // 保存原始状态
            int[] oldAvailable = new int[resourceCount];
            System.arraycopy(available, 0, oldAvailable, 0, resourceCount);
            int[] oldAllocation = new int[resourceCount];
            System.arraycopy(allocation[process], 0, oldAllocation, 0, resourceCount);
            int[] oldNeed = new int[resourceCount];
            System.arraycopy(need[process], 0, oldNeed, 0, resourceCount);

            // 模拟分配
            for (int j = 0; j < resourceCount; j++) {
                available[j] -= request[j];
                allocation[process][j] += request[j];
                need[process][j] -= request[j];
            }

            outputArea.append("分配后状态:\n");
            outputArea.append("可用资源: A=" + available[0] + " B=" + available[1] + " C=" + available[2] + "\n");

            // 检查安全性
            boolean safe = isSafe();

            if (safe) {
                outputArea.append("\n请求可以满足！系统仍处于安全状态。\n");
            } else {
                // 恢复原始状态
                System.arraycopy(oldAvailable, 0, available, 0, resourceCount);
                System.arraycopy(oldAllocation, 0, allocation[process], 0, resourceCount);
                System.arraycopy(oldNeed, 0, need[process], 0, resourceCount);

                outputArea.append("\n请求不能满足！分配会导致系统进入不安全状态。\n");
                outputArea.append("已恢复原始状态。\n");
            }
        } catch (NumberFormatException e) {
            outputArea.append("错误: 请输入有效的数字！\n");
        }
    }

    // 检查系统是否处于安全状态
    private boolean isSafe() {
        // 工作向量
        int[] work = new int[resourceCount];
        System.arraycopy(available, 0, work, 0, resourceCount);

        // 初始化finish数组
        boolean[] finish = new boolean[processCount];

        int count = 0;

        while (count < processCount) {
            boolean found = false;
            for (int i = 0; i < processCount; i++) {
                if (!finish[i] && checkNeedLessThanWork(i, work)) {
                    // 分配资源给进程i
                    for (int j = 0; j < resourceCount; j++) {
                        work[j] += allocation[i][j];
                    }

                    count++;
                    finish[i] = true;
                    found = true;

                    outputArea.append("找到可执行进程: P" + (i + 1) + "\n");
                    outputArea.append("工作向量变为: A=" + work[0] + " B=" + work[1] + " C=" + work[2] + "\n");
                }
            }

            if (!found) {
                break;
            }
        }

        return count == processCount;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Task4().setVisible(true);
            }
        });
    }
}