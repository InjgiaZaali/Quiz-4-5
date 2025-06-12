import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {

            ConfigLoader config = new ConfigLoader("config.properties");
            ChatBotManager bot = new ChatBotManager(config.getApiUrl());

            System.out.println("👋 Welcome! I'm " + config.getBotName());
            System.out.println("🚀 Ready to manage your blog posts!");

            while (true) {
                System.out.println("\n📋 Menu:");
                System.out.println("1. Create a new blog post");
                System.out.println("2. View all blog posts");
                System.out.println("3. View site statistics");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        String title = "";
                        while (title.trim().isEmpty()) {
                            System.out.print("📝 Enter blog title: ");
                            title = scanner.nextLine();
                            if (title.trim().isEmpty()) {
                                System.out.println("❌ Title cannot be empty. Please try again.");
                            }
                        }

                        String content = "";
                        while (content.trim().isEmpty()) {
                            System.out.print("📄 Enter blog content: ");
                            content = scanner.nextLine();
                            if (content.trim().isEmpty()) {
                                System.out.println("❌ Content cannot be empty. Please try again.");
                            }
                        }

                        System.out.print("👤 Enter author (optional, press Enter to skip): ");
                        String author = scanner.nextLine();

                        System.out.println("\n⏳ Creating blog post...");
                        String createResponse = bot.createPost(
                                title.trim(),
                                content.trim(),
                                author.trim().isEmpty() ? null : author.trim()
                        );
                        System.out.println(createResponse);
                        break;

                    case "2":
                        System.out.println("\n⏳ Fetching all blog posts...");
                        String postsResponse = bot.viewAllPosts();
                        System.out.println(postsResponse);
                        break;

                    case "3":
                        System.out.println("\n⏳ Fetching statistics...");
                        String statsResponse = bot.viewStatistics();
                        System.out.println(statsResponse);
                        break;

                    case "0":
                        System.out.println("👋 Goodbye! Thank you for using " + config.getBotName() + "!");
                        return;

                    default:
                        System.out.println("❌ Invalid input. Please choose a number from 0-3.");
                }
            }

        } catch (Exception e) {
            System.out.println("❗ Application Error: " + e.getMessage());
            System.out.println("💡 Please check your configuration and try again.");
            e.printStackTrace();
        }
    }
}
