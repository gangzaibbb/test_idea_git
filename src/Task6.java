import java.util.*;

public class Task6 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("磁盘移臂调度算法模拟程序");
        System.out.println("------------------------");

        // 输入磁道请求序列
        System.out.print("请输入磁道请求序列(用空格分隔，例如: 98 183 37 122 14 124 65 67): ");
        String input = scanner.nextLine();
        String[] requestsStr = input.split(" ");
        List<Integer> requests = new ArrayList<>();
        for (String req : requestsStr) {
            requests.add(Integer.parseInt(req));
        }

        // 输入当前磁头位置
        System.out.print("请输入当前磁头位置: ");
        int currentPosition = scanner.nextInt();

        // 输入磁道总数
        System.out.print("请输入磁盘磁道总数: ");
        int totalTracks = scanner.nextInt();

        System.out.println("\n调度结果:");
        System.out.println("请求序列: " + requests);
        System.out.println("当前磁头位置: " + currentPosition);

        sstf(requests, currentPosition);
        scan(requests, currentPosition, totalTracks);

        scanner.close();
    }

    // SSTF算法
    public static void sstf(List<Integer> requests, int currentPosition) {
        // 结果
        List<Integer> sequence = new ArrayList<>();
        List<Integer> remainingRequests = new ArrayList<>(requests);
        int totalMovement = 0;
        int current = currentPosition;

        System.out.println("\nSSTF算法:");

        while (!remainingRequests.isEmpty()) {
            // 找到距离当前磁头位置最近的请求
            int minDistance = Integer.MAX_VALUE;
            int nextRequest = -1;
            int indexToRemove = -1;

            for (int i = 0; i < remainingRequests.size(); i++) {
                int distance = Math.abs(remainingRequests.get(i) - current);
                if (distance < minDistance) {
                    minDistance = distance;
                    nextRequest = remainingRequests.get(i);
                    indexToRemove = i;
                }
            }

            // 处理该请求
            sequence.add(nextRequest);
            totalMovement += minDistance;
            current = nextRequest;
            remainingRequests.remove(indexToRemove);

            System.out.println("移动到磁道: " + nextRequest + ", 移动距离: " + minDistance);
        }

        System.out.println("\n请求响应顺序: " + sequence);
        System.out.println("总移臂量: " + totalMovement);
    }

    // SCAN算法
    public static void scan(List<Integer> requests, int currentPosition, int totalTracks) {
        List<Integer> sequence = new ArrayList<>();
        List<Integer> remainingRequests = new ArrayList<>(requests);
        int totalMovement = 0;
        int current = currentPosition;
        boolean direction = true; // true表示向外(磁道号增加方向)，false表示向内

        System.out.println("\nSCAN算法:");
        System.out.println("初始方向: " + (direction ? "向外(磁道号增加)" : "向内(磁道号减少)"));

        while (!remainingRequests.isEmpty()) {
            if (direction) {
                // 向外扫描
                int nextRequest = -1;
                int minDistance = Integer.MAX_VALUE;
                int indexToRemove = -1;

                // 查找当前方向上最近的请求
                for (int i = 0; i < remainingRequests.size(); i++) {
                    int request = remainingRequests.get(i);
                    if (request >= current) {
                        int distance = request - current;
                        if (distance < minDistance) {
                            minDistance = distance;
                            nextRequest = request;
                            indexToRemove = i;
                        }
                    }
                }

                if (nextRequest != -1) {
                    // 处理该请求
                    sequence.add(nextRequest);
                    totalMovement += minDistance;
                    current = nextRequest;
                    remainingRequests.remove(indexToRemove);
                    System.out.println("移动到磁道: " + nextRequest + ", 移动距离: " + minDistance);
                } else {
                    // 到达最外层，改变方向
                    totalMovement += (totalTracks - 1 - current);
                    System.out.println("到达最外层磁道 " + (totalTracks - 1) +
                            ", 移动距离: " + (totalTracks - 1 - current));
                    current = totalTracks - 1;
                    direction = false;
                    System.out.println("改变方向为向内");
                }
            } else {
                // 向内扫描
                int nextRequest = -1;
                int minDistance = Integer.MAX_VALUE;
                int indexToRemove = -1;

                // 查找当前方向上最近的请求
                for (int i = 0; i < remainingRequests.size(); i++) {
                    int request = remainingRequests.get(i);
                    if (request <= current) {
                        int distance = current - request;
                        if (distance < minDistance) {
                            minDistance = distance;
                            nextRequest = request;
                            indexToRemove = i;
                        }
                    }
                }

                if (nextRequest != -1) {
                    // 处理该请求
                    sequence.add(nextRequest);
                    totalMovement += minDistance;
                    current = nextRequest;
                    remainingRequests.remove(indexToRemove);
                    System.out.println("移动到磁道: " + nextRequest + ", 移动距离: " + minDistance);
                } else {
                    // 到达最内层，改变方向
                    totalMovement += current;
                    System.out.println("到达最内层磁道 0, 移动距离: " + current);
                    current = 0;
                    direction = true;
                    System.out.println("改变方向为向外");
                }
            }
        }

        System.out.println("\n请求响应顺序: " + sequence);
        System.out.println("总移臂量: " + totalMovement);
    }
}