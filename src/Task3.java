import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;

public class Task3 extends JFrame {
    private static final int BUFFER_SIZE = 10;
    private final JTextArea outputArea;
    private final JTextField producerField;
    private final JTextField consumerField;
    private final Buffer buffer;

    public Task3() {
        super("生产者/消费者问题模拟");

        buffer = new Buffer(BUFFER_SIZE);

        // 创建UI组件
        outputArea = new JTextArea(20, 40);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        producerField = new JTextField(5);
        consumerField = new JTextField(5);

        JButton startButton = new JButton("开始模拟");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });

        // 设置布局
        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("生产者数量:"));
        controlPanel.add(producerField);
        controlPanel.add(new JLabel("消费者数量:"));
        controlPanel.add(consumerField);
        controlPanel.add(startButton);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(controlPanel, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    private void startSimulation() {
        try {
            int numProducers = Integer.parseInt(producerField.getText());
            int numConsumers = Integer.parseInt(consumerField.getText());

            outputArea.setText(""); // 清空输出区域

            // 创建并启动生产者线程
            for (int i = 0; i < numProducers; i++) {
                new Thread(new Producer(buffer, i+1, outputArea)).start();
            }

            // 创建并启动消费者线程
            for (int i = 0; i < numConsumers; i++) {
                new Thread(new Consumer(buffer, i+1, outputArea)).start();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字!", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Task3();
            }
        });
    }
}

// 缓冲区类
class Buffer {
    private final int[] buffer;
    private final Semaphore mutex; // 互斥信号量
    private final Semaphore empty;  // 空缓冲区信号量
    private final Semaphore full;   // 满缓冲区信号量
    private int in = 0;             // 生产者放入产品的位置
    private int out = 0;            // 消费者取出产品的位置

    public Buffer(int size) {
        buffer = new int[size];
        mutex = new Semaphore(1);    // 二进制信号量，用于互斥
        empty = new Semaphore(size); // 初始时缓冲区全空
        full = new Semaphore(0);    // 初始时缓冲区无产品
    }

    public void insert(int item, int producerId) throws InterruptedException {
        empty.acquire();  // 等待缓冲区有空位
        mutex.acquire();  // 进入临界区

        buffer[in] = item;
        in = (in + 1) % buffer.length;

        mutex.release();  // 离开临界区
        full.release();   // 增加一个满缓冲区
    }

    public int remove(int consumerId) throws InterruptedException {
        full.acquire();   // 等待缓冲区有产品
        mutex.acquire();  // 进入临界区

        int item = buffer[out];
        out = (out + 1) % buffer.length;

        mutex.release();  // 离开临界区
        empty.release();  // 增加一个空缓冲区

        return item;
    }
}

// 生产者类
class Producer implements Runnable {
    private final Buffer buffer;
    private final int id;
    private final JTextArea outputArea;
    private static int itemCount = 0;

    public Producer(Buffer buffer, int id, JTextArea outputArea) {
        this.buffer = buffer;
        this.id = id;
        this.outputArea = outputArea;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // 模拟生产时间
                Thread.sleep((int)(Math.random() * 2000));

                int item = ++itemCount;
                buffer.insert(item, id);

                SwingUtilities.invokeLater(() -> {
                    outputArea.append(String.format("生产者 %d 生产了产品 %d\n", id, item));
                });
            }
        } catch (InterruptedException e) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

// 消费者类
class Consumer implements Runnable {
    private final Buffer buffer;
    private final int id;
    private final JTextArea outputArea;

    public Consumer(Buffer buffer, int id, JTextArea outputArea) {
        this.buffer = buffer;
        this.id = id;
        this.outputArea = outputArea;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // 模拟消费时间
                Thread.sleep((int)(Math.random() * 2000));

                int item = buffer.remove(id);

                SwingUtilities.invokeLater(() -> {
                    outputArea.append(String.format("消费者 %d 消费了产品 %d\n", id, item));
                });
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}