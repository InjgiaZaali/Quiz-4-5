import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            ConfigLoader config = new ConfigLoader("config.properties");
            ChatBotManager bot = new ChatBotManager(config.getApiUrl());
            Scanner scanner = new Scanner(System.in);

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
                        System.out.print("📝 Enter blog title: ");
                        String title = scanner.nextLine();
                        System.out.print("📄 Enter blog content: ");
                        String content = scanner.nextLine();
                        System.out.print("👤 Enter author (optional, press Enter to skip): ");
                        String author = scanner.nextLine();

                        System.out.println("\n⏳ Creating blog post...");
                        String createResponse = bot.createPost(
                                title,
                                content,
                                author.isEmpty() ? null : author
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
                        System.out.println("❌ Invalid input. Please choose a number from 0-4.");
                }
            }

        } catch (Exception e) {
            System.out.println("❗ Application Error: " + e.getMessage());
            System.out.println("💡 Please check your configuration and try again.");
            e.printStackTrace();
        }
    }
}