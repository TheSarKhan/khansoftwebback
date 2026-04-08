package az.khansoft.api.config;

import az.khansoft.api.entity.*;
import az.khansoft.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final SiteSettingRepository settingRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.full-name}")
    private String adminFullName;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedServices();
        seedProjects();
        seedTeam();
        seedSettings();
    }

    private void seedAdmin() {
        if (userRepository.count() > 0) return;
        if (adminPassword == null || adminPassword.isBlank()) {
            log.warn("ADMIN_PASSWORD is not set — skipping admin user seeding. "
                    + "Set ADMIN_PASSWORD env var on first run to create the admin account.");
            return;
        }
        userRepository.save(User.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .fullName(adminFullName)
                .email(adminEmail)
                .role("ADMIN")
                .build());
        log.info("Seeded admin user '{}'", adminUsername);
    }

    private void seedServices() {
        if (serviceRepository.count() > 0) return;

        serviceRepository.save(Service.builder()
                .title("Web development").titleAz("Web development")
                .description("Fast, SEO-friendly websites and web apps built for conversion.")
                .descriptionAz("Sürətli, SEO dostu və konversiyaya hesablanmış müasir veb saytlar və veb tətbiqlər.")
                .icon("Globe").sortOrder(1).active(true).build());

        serviceRepository.save(Service.builder()
                .title("Mobile app").titleAz("Mobil tətbiq")
                .description("iOS and Android apps from a single codebase — less cost, faster delivery.")
                .descriptionAz("iOS və Android üçün eyni keyfiyyətdə işləyən mobil tətbiqlər.")
                .icon("Smartphone").sortOrder(2).active(true).build());

        serviceRepository.save(Service.builder()
                .title("Cybersecurity").titleAz("Kibertəhlükəsizlik")
                .description("Audit, penetration testing and continuous protection for your business.")
                .descriptionAz("Audit, penetrasiya testi və davamlı qoruma — biznesinizi və müştəri məlumatınızı qoruyun.")
                .icon("ShieldCheck").sortOrder(3).active(true).build());

        serviceRepository.save(Service.builder()
                .title("Infrastructure").titleAz("İnfrastruktur")
                .description("Your site and systems stay online, fast and monitored — server, hosting, alerting.")
                .descriptionAz("Saytınız və sistemləriniz dayanmadan, sürətlə işləsin — server, hosting, monitorinq.")
                .icon("Server").sortOrder(4).active(true).build());

        serviceRepository.save(Service.builder()
                .title("Automation").titleAz("Avtomatlaşdırma")
                .description("Turn manual processes into systems — win back 20+ hours a week.")
                .descriptionAz("Manual prosesləri sistemlərə çevirin — həftədə 20+ saatınızı geri qazanın.")
                .icon("Zap").sortOrder(5).active(true).build());

        serviceRepository.save(Service.builder()
                .title("Business analytics").titleAz("Biznes analitika")
                .description("Dashboards and reporting systems that turn data into decisions.")
                .descriptionAz("Məlumatınızı qərara çevirən dashboard və hesabat sistemləri.")
                .icon("BarChart3").sortOrder(6).active(true).build());

        log.info("Seeded 6 services");
    }

    private void seedProjects() {
        if (projectRepository.count() > 0) return;

        projectRepository.save(Project.builder()
                .title("testup.az")
                .description("Müasir imtahan və test platforması — sıfırdan qurulub, davamlı dəstəklənir. Fikirdən canlı məhsula, sabit qiymətlə.")
                .category("Web platforma")
                .technologies("Next.js, Spring Boot, PostgreSQL, Docker, REST API")
                .projectUrl("https://testup.az")
                .featured(true).active(true).build());

        log.info("Seeded 1 project");
    }

    private void seedTeam() {
        if (teamMemberRepository.count() > 0) return;

        teamMemberRepository.save(TeamMember.builder()
                .fullName("Sərxan Babayev")
                .position("Founder & Technical Lead").positionAz("Qurucu & Texniki Rəhbər")
                .bio("Müştərilərlə birbaşa işləyir, layihələrin texniki istiqamətini idarə edir.")
                .sortOrder(1).active(true).build());

        log.info("Seeded 1 team member");
    }

    private void seedSettings() {
        if (settingRepository.count() > 0) return;

        settingRepository.save(SiteSetting.builder()
                .settingKey("site_title").settingValue("KhanSoft")
                .description("Site title").build());
        settingRepository.save(SiteSetting.builder()
                .settingKey("site_description").settingValue("Biznesinizin texniki tərəfi artıq sizin dərdiniz deyil")
                .description("Site tagline").build());
        settingRepository.save(SiteSetting.builder()
                .settingKey("contact_email").settingValue("sarxanbabayevcontact@gmail.com")
                .description("Contact email").build());
        settingRepository.save(SiteSetting.builder()
                .settingKey("contact_phone").settingValue("+994 50 201 71 64")
                .description("Contact phone").build());
        settingRepository.save(SiteSetting.builder()
                .settingKey("contact_whatsapp").settingValue("+994 50 201 71 64")
                .description("WhatsApp number").build());
        settingRepository.save(SiteSetting.builder()
                .settingKey("address").settingValue("Bakı, Azərbaycan")
                .description("Office address").build());
        settingRepository.save(SiteSetting.builder()
                .settingKey("working_hours").settingValue("B.e – Cümə, 09:00 – 18:00")
                .description("Working hours").build());

        log.info("Seeded 7 site settings");
    }
}
