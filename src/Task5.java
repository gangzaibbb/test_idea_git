import java.util.*;
// 1 2 3 4 1 2 5 1 2 3 4 5 4 2 1 5 2 6 2 4
public class Task5 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 输入页面引用序列
        System.out.print("请输入页面引用序列(用空格分隔，例如：1 2 3 4 1 2): ");
        String input = scanner.nextLine();
        String[] pagesStr = input.split(" ");
        int[] pageReferences = new int[pagesStr.length];
        for (int i = 0; i < pagesStr.length; i++) {
            pageReferences[i] = Integer.parseInt(pagesStr[i].trim());
        }

        // 输入分配的页框数
        System.out.print("请输入分配的页框数: ");
        int frameCount = scanner.nextInt();

        // 运行三种算法
        System.out.println("\nOPTIMAL 算法结果:");
        simulateOPTIMAL(pageReferences, frameCount);

        System.out.println("\nLRU 算法结果:");
        simulateLRU(pageReferences, frameCount);

        System.out.println("\nFIFO 算法结果:");
        simulateFIFO(pageReferences, frameCount);

        scanner.close();
    }

    // 最优(OPTIMAL)算法
    public static void simulateOPTIMAL(int[] pageReferences, int frameCount) {
        // 内存页
        Set<Integer> frames = new HashSet<>();
        List<Integer> frameList = new ArrayList<>();
        int pageFaults = 0;
        List<Integer> evictedPages = new ArrayList<>();

        for (int i = 0; i < pageReferences.length; i++) {
            int page = pageReferences[i];

            // 缺页
            if (!frames.contains(page)) {
                pageFaults++;

                // 未放满
                if (frames.size() < frameCount) {
                    frames.add(page);
                    frameList.add(page);
                } else {
                    // 寻找后面最长时间不会被使用的页面
                    int farthest = -1, idxToReplace = -1;
                    for (int j = 0; j < frameList.size(); j++) {
                        int currentFrame = frameList.get(j);
                        int k;
                        for (k = i + 1; k < pageReferences.length; k++) {
                            if (pageReferences[k] == currentFrame) {
                                break;
                            }
                        }

                        if (k == pageReferences.length) {
                            idxToReplace = j;
                            break;
                        } else if (k > farthest) {
                            farthest = k;
                            idxToReplace = j;
                        }
                    }

                    // 置换
                    int evictedPage = frameList.get(idxToReplace);
                    evictedPages.add(evictedPage);
                    frames.remove(evictedPage);
                    frameList.set(idxToReplace, page);
                    frames.add(page);
                }
            }

            // 打印当前状态
            System.out.print("引用 " + page + ": [");
            for (int j = 0; j < frameList.size(); j++) {
                System.out.print(frameList.get(j));
                if (j < frameList.size() - 1) {
                    System.out.print(", ");
                }
            }
            for (int j = frameList.size(); j < frameCount; j++) {
                System.out.print((j == 0 ? "" : ", ") + " ");
            }
            System.out.println("] " + (frames.contains(page) ? "" : "缺页"));
        }

        printStatistics(evictedPages, pageFaults, pageReferences.length);
    }

    // LRU算法
    public static void simulateLRU(int[] pageReferences, int frameCount) {
        Set<Integer> frames = new HashSet<>();
        Map<Integer, Integer> lastUsed = new HashMap<>();
        int pageFaults = 0;
        List<Integer> evictedPages = new ArrayList<>();

        for (int i = 0; i < pageReferences.length; i++) {
            int page = pageReferences[i];
            lastUsed.put(page, i);

            if (!frames.contains(page)) {
                pageFaults++;

                if (frames.size() < frameCount) {
                    frames.add(page);
                } else {
                    // 找到前面最近最少使用的页面
                    int lru = Integer.MAX_VALUE, pageToEvict = -1;
                    for (int p : frames) {
                        if (lastUsed.get(p) < lru) {
                            lru = lastUsed.get(p);
                            pageToEvict = p;
                        }
                    }

                    // 置换
                    evictedPages.add(pageToEvict);
                    frames.remove(pageToEvict);
                    frames.add(page);
                }
            }

            // 打印当前状态
            printCurrentState(frames, frameCount, page);
        }

        printStatistics(evictedPages, pageFaults, pageReferences.length);
    }

    // FIFO算法
    public static void simulateFIFO(int[] pageReferences, int frameCount) {
        Set<Integer> frames = new HashSet<>();
        // 用队列记录进入内存顺序
        Queue<Integer> queue = new LinkedList<>();
        int pageFaults = 0;
        List<Integer> evictedPages = new ArrayList<>();

        for (int page : pageReferences) {
            if (!frames.contains(page)) {
                pageFaults++;

                if (frames.size() < frameCount) {
                    frames.add(page);
                    queue.add(page);
                } else {
                    int pageToEvict = queue.poll();
                    evictedPages.add(pageToEvict);
                    frames.remove(pageToEvict);
                    frames.add(page);
                    queue.add(page);
                }
            }

            // 打印当前状态
            printCurrentState(frames, frameCount, page);
        }

        printStatistics(evictedPages, pageFaults, pageReferences.length);
    }

    // 打印当前内存状态
    private static void printCurrentState(Set<Integer> frames, int frameCount, int currentPage) {
        System.out.print("引用 " + currentPage + ": [");
        int i = 0;
        for (int page : frames) {
            if (i > 0) {
                System.out.print(", ");
            }
            System.out.print(page);
            i++;
        }
        for (; i < frameCount; i++) {
            System.out.print((i == 0 ? "" : ", ") + " ");
        }
        System.out.println("] " + (frames.contains(currentPage) ? "" : "缺页"));
    }

    // 打印统计信息
    private static void printStatistics(List<Integer> evictedPages, int pageFaults, int totalReferences) {
        System.out.println("\n淘汰的页号序列: " + evictedPages);
        System.out.println("缺页次数: " + pageFaults);
        double faultRate = (double) pageFaults / totalReferences * 100;
        System.out.printf("缺页率: %.2f%%\n", faultRate);
    }
}