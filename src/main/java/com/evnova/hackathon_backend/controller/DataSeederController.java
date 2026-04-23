package com.evnova.hackathon_backend.controller;

import com.evnova.hackathon_backend.enums.Role;
import com.evnova.hackathon_backend.model.*;
import com.evnova.hackathon_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * One-shot data seeder endpoint. Call POST /api/seed once to populate the DB
 * with realistic dummy data. Idempotent — skips if data already exists.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DataSeederController {

    private final UserRepository userRepository;
    private final HackathonRepository hackathonRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final SubmissionRepository submissionRepository;
    private final ParticipantRepository participantRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/seed")
    public ResponseEntity<Map<String, Object>> seed() {
        // Idempotency guard
        if (userRepository.count() > 0) {
            return ResponseEntity.ok(Map.of(
                "status", "skipped",
                "message", "Data already exists. Drop tables and restart if you want a fresh seed."
            ));
        }

        String hashedPwd = passwordEncoder.encode("Password123!");

        // ──────────────────────────────────────────
        // 1. USERS
        // ──────────────────────────────────────────
        User org1 = userRepository.save(User.builder()
                .name("Dr. Anika Sharma").email("anika@evnova.dev").password(hashedPwd)
                .role(Role.ORGANIZER).createdAt(LocalDateTime.now().minusDays(90)).build());
        User org2 = userRepository.save(User.builder()
                .name("Rohan Mehta").email("rohan@evnova.dev").password(hashedPwd)
                .role(Role.ORGANIZER).createdAt(LocalDateTime.now().minusDays(80)).build());
        User org3 = userRepository.save(User.builder()
                .name("Priya Kapoor").email("priya@evnova.dev").password(hashedPwd)
                .role(Role.ORGANIZER).createdAt(LocalDateTime.now().minusDays(70)).build());

        User p1  = userRepository.save(user("Vikram Singh",      "vikram@dev.com",  hashedPwd));
        User p2  = userRepository.save(user("Sneha Rao",         "sneha@dev.com",   hashedPwd));
        User p3  = userRepository.save(user("Arjun Nair",        "arjun@dev.com",   hashedPwd));
        User p4  = userRepository.save(user("Riya Desai",        "riya@dev.com",    hashedPwd));
        User p5  = userRepository.save(user("Kabir Sinha",       "kabir@dev.com",   hashedPwd));
        User p6  = userRepository.save(user("Meera Pillai",      "meera@dev.com",   hashedPwd));
        User p7  = userRepository.save(user("Aarav Gupta",       "aarav@dev.com",   hashedPwd));
        User p8  = userRepository.save(user("Tanvi Joshi",       "tanvi@dev.com",   hashedPwd));
        User p9  = userRepository.save(user("Dev Malhotra",      "dev@dev.com",     hashedPwd));
        User p10 = userRepository.save(user("Sara Iyer",         "sara@dev.com",    hashedPwd));
        User p11 = userRepository.save(user("Nikhil Pandey",     "nikhil@dev.com",  hashedPwd));
        User p12 = userRepository.save(user("Ishaan Bhatia",     "ishaan@dev.com",  hashedPwd));

        // ──────────────────────────────────────────
        // 2. HACKATHONS
        // ──────────────────────────────────────────
        Hackathon hackAI = hackathonRepository.save(Hackathon.builder()
                .title("HackAI 2025")
                .shortDescription("Build the future with AI & Machine Learning.")
                .description("HackAI 2025 challenges participants to create cutting-edge AI/ML solutions that solve real-world problems. From NLP to computer vision, bring your best ideas and compete for $10,000 in prizes.")
                .type("online")
                .themes(List.of("AI/ML", "Python", "TensorFlow", "NLP", "Computer Vision"))
                .prizePool(10000.0)
                .startDate(LocalDate.now().minusDays(60))
                .endDate(LocalDate.now().minusDays(30))
                .registrationDeadline(LocalDate.now().minusDays(65))
                .maxTeamSize(4)
                .organizer(org1)
                .status("COMPLETED")
                .problemStatement("Design an AI system that can detect early signs of mental health issues from anonymous user-written text, with privacy-preserving federated learning.")
                .goals(List.of("Innovate in AI/ML space", "Build privacy-first solutions", "Tackle mental health crisis with tech"))
                .rules(List.of("Teams of 2-4 members", "Open source stack only", "Must submit GitHub repo + demo video", "No pre-built ML SaaS tools"))
                .bannerImageUrl("https://images.unsplash.com/photo-1677442135703-1787eea5ce01?w=800&auto=format&fit=crop")
                .createdAt(LocalDateTime.now().minusDays(90))
                .build());

        Hackathon chainReact = hackathonRepository.save(Hackathon.builder()
                .title("ChainReact Hackathon")
                .shortDescription("Decentralize the world — DeFi, NFTs & Web3.")
                .description("ChainReact brings together blockchain builders to create the next generation of decentralized applications. Build on Ethereum, Solana, or Polygon and compete for $8,000 in prizes.")
                .type("online")
                .themes(List.of("Blockchain", "Solidity", "DeFi", "Web3", "NFT"))
                .prizePool(8000.0)
                .startDate(LocalDate.now().minusDays(45))
                .endDate(LocalDate.now().minusDays(15))
                .registrationDeadline(LocalDate.now().minusDays(50))
                .maxTeamSize(3)
                .organizer(org2)
                .status("COMPLETED")
                .problemStatement("Build a decentralized micro-lending protocol that enables peer-to-peer loans without traditional credit scores, using on-chain reputation.")
                .goals(List.of("Advance DeFi accessibility", "Build trustless financial systems", "Reduce barriers for unbanked populations"))
                .rules(List.of("Teams of 2-3 members", "Must deploy on testnet", "Smart contract code must be auditable", "UI must be a working dApp"))
                .bannerImageUrl("https://images.unsplash.com/photo-1639762681485-074b7f938ba0?w=800&auto=format&fit=crop")
                .createdAt(LocalDateTime.now().minusDays(70))
                .build());

        Hackathon mediHack = hackathonRepository.save(Hackathon.builder()
                .title("MediHack 2025")
                .shortDescription("Tech meets healthcare — innovate for lives.")
                .description("MediHack challenges developers, designers, and medical professionals to collaborate and build the next breakthrough in health technology. $5,000 in prizes await the most impactful solutions.")
                .type("offline")
                .themes(List.of("Healthcare", "React", "Node.js", "IoT", "Data Analytics"))
                .prizePool(5000.0)
                .startDate(LocalDate.now().minusDays(3))
                .endDate(LocalDate.now().plusDays(25))
                .registrationDeadline(LocalDate.now().minusDays(7))
                .maxTeamSize(5)
                .organizer(org3)
                .status("ACTIVE")
                .problemStatement("Create a real-time patient monitoring dashboard that aggregates data from wearables and hospital IoT sensors to predict deterioration 4 hours in advance.")
                .goals(List.of("Improve patient outcomes", "Reduce ICU response time", "Enable proactive medical care"))
                .rules(List.of("Teams of 2-5 members", "Prototype must be demonstrable", "Must use at least one real-world dataset", "Judged on innovation, feasibility, and impact"))
                .bannerImageUrl("https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=800&auto=format&fit=crop")
                .createdAt(LocalDateTime.now().minusDays(30))
                .build());

        Hackathon greenTech = hackathonRepository.save(Hackathon.builder()
                .title("GreenTech Sprint")
                .shortDescription("Code for the planet — sustainability starts here.")
                .description("GreenTech Sprint challenges innovators to build solutions that address climate change, energy efficiency, and sustainable living. Win from a $6,000 prize pool and make a real impact.")
                .type("online")
                .themes(List.of("CleanTech", "IoT", "Sustainability", "Energy", "Python"))
                .prizePool(6000.0)
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().plusDays(20))
                .registrationDeadline(LocalDate.now().minusDays(10))
                .maxTeamSize(4)
                .organizer(org1)
                .status("ACTIVE")
                .problemStatement("Design a smart energy management system for residential areas that reduces carbon footprint by at least 30% using machine learning predictions and IoT device control.")
                .goals(List.of("Reduce residential carbon emissions", "Make green energy accessible", "Create scalable sustainability solutions"))
                .rules(List.of("Teams of 2-4 members", "Must include a working prototype", "Solution must be measurably impactful", "Open source submission required"))
                .bannerImageUrl("https://images.unsplash.com/photo-1473341304170-971dccb5ac1e?w=800&auto=format&fit=crop")
                .createdAt(LocalDateTime.now().minusDays(20))
                .build());

        Hackathon eduThon = hackathonRepository.save(Hackathon.builder()
                .title("EduThon 2025")
                .shortDescription("Reimagine education with AR, VR & AI.")
                .description("EduThon 2025 invites builders to revolutionize how we learn. Use AR, VR, AI, and gamification to create the classroom of the future. $4,000 in prizes for the most innovative EdTech solutions.")
                .type("online")
                .themes(List.of("Education", "AR/VR", "JavaScript", "AI", "Gamification"))
                .prizePool(4000.0)
                .startDate(LocalDate.now().plusDays(15))
                .endDate(LocalDate.now().plusDays(45))
                .registrationDeadline(LocalDate.now().plusDays(10))
                .maxTeamSize(4)
                .organizer(org2)
                .status("UPCOMING")
                .problemStatement("Build an adaptive learning platform using AI to personalize curriculum for students with different learning abilities, making quality education accessible globally.")
                .goals(List.of("Democratize quality education", "Support neurodiverse learners", "Gamify the learning experience"))
                .rules(List.of("Teams of 2-4 members", "Must target K-12 or higher education", "Accessibility compliance required", "Working demo mandatory for finals"))
                .bannerImageUrl("https://images.unsplash.com/photo-1580582932707-520aed937b7b?w=800&auto=format&fit=crop")
                .createdAt(LocalDateTime.now().minusDays(5))
                .build());

        // ──────────────────────────────────────────
        // 3. TEAMS for COMPLETED hackathons
        // ──────────────────────────────────────────

        // HackAI 2025 Teams
        Team neuralNinjas = teamRepository.save(team("Neural Ninjas", hackAI, p1));
        Team alphaBuilders = teamRepository.save(team("AlphaBuilders", hackAI, p3));
        Team codeWizards  = teamRepository.save(team("Code Wizards", hackAI, p5));
        Team byteForce    = teamRepository.save(team("Byte Force", hackAI, p7));

        // ChainReact Teams
        Team defiDynamos  = teamRepository.save(team("DeFi Dynamos", chainReact, p2));
        Team blockBusters = teamRepository.save(team("BlockBusters", chainReact, p4));
        Team web3Warriors = teamRepository.save(team("Web3 Warriors", chainReact, p6));

        // MediHack Teams (active — no submissions yet)
        Team healthHackers = teamRepository.save(team("HealthHackers", mediHack, p8));
        Team vitalTech     = teamRepository.save(team("VitalTech", mediHack, p9));

        // GreenTech Teams (active)
        Team ecoBuilders   = teamRepository.save(team("EcoBuilders", greenTech, p10));
        Team solarSurge    = teamRepository.save(team("SolarSurge", greenTech, p11));

        // ──────────────────────────────────────────
        // 4. TEAM MEMBERS
        // ──────────────────────────────────────────
        // Neural Ninjas
        teamMember(neuralNinjas, p1, "LEADER");
        teamMember(neuralNinjas, p2, "MEMBER");
        teamMember(neuralNinjas, p3, "MEMBER");
        // AlphaBuilders
        teamMember(alphaBuilders, p3, "LEADER");
        teamMember(alphaBuilders, p4, "MEMBER");
        // Code Wizards
        teamMember(codeWizards, p5, "LEADER");
        teamMember(codeWizards, p6, "MEMBER");
        teamMember(codeWizards, p7, "MEMBER");
        // Byte Force
        teamMember(byteForce, p7, "LEADER");
        teamMember(byteForce, p8, "MEMBER");
        // DeFi Dynamos
        teamMember(defiDynamos, p2, "LEADER");
        teamMember(defiDynamos, p9, "MEMBER");
        teamMember(defiDynamos, p10, "MEMBER");
        // BlockBusters
        teamMember(blockBusters, p4, "LEADER");
        teamMember(blockBusters, p11, "MEMBER");
        // Web3 Warriors
        teamMember(web3Warriors, p6, "LEADER");
        teamMember(web3Warriors, p12, "MEMBER");
        // Active teams
        teamMember(healthHackers, p8, "LEADER");
        teamMember(healthHackers, p9, "MEMBER");
        teamMember(vitalTech, p9, "LEADER");
        teamMember(vitalTech, p10, "MEMBER");
        teamMember(ecoBuilders, p10, "LEADER");
        teamMember(ecoBuilders, p11, "MEMBER");
        teamMember(solarSurge, p11, "LEADER");
        teamMember(solarSurge, p12, "MEMBER");

        // ──────────────────────────────────────────
        // 5. SUBMISSIONS & SCORES (completed hackathons)
        // ──────────────────────────────────────────
        LocalDateTime submittedAt = LocalDateTime.now().minusDays(30);

        // HackAI 2025 submissions
        submissionRepository.save(submission(neuralNinjas, hackAI, "MindGuard AI",
                "Federated learning model for mental health detection with <1% data leakage.",
                "https://github.com/neural-ninjas/mindguard",
                "https://mindguard.demo.app", 38.5, submittedAt));
        submissionRepository.save(submission(alphaBuilders, hackAI, "EmotiSense",
                "Real-time NLP sentiment pipeline with BERT fine-tuned on clinical notes.",
                "https://github.com/alphabuilders/emotisense",
                null, 35.0, submittedAt.minusHours(2)));
        submissionRepository.save(submission(codeWizards, hackAI, "TherapyBot 3000",
                "Conversational AI therapist with CBT-based response generation.",
                "https://github.com/code-wizards/therapybot",
                "https://therapybot.demo.app", 31.5, submittedAt.minusHours(4)));
        submissionRepository.save(submission(byteForce, hackAI, "SerenityScore",
                "Gamified mental wellness tracker powered by random forest classifier.",
                "https://github.com/byte-force/serenityscore",
                null, 27.0, submittedAt.minusHours(6)));

        // ChainReact submissions
        LocalDateTime crSubmitted = LocalDateTime.now().minusDays(15);
        submissionRepository.save(submission(defiDynamos, chainReact, "TrustLend Protocol",
                "On-chain reputation-based micro-lending with zero-knowledge credit proofs.",
                "https://github.com/defi-dynamos/trustlend",
                "https://trustlend.vercel.app", 37.0, crSubmitted));
        submissionRepository.save(submission(blockBusters, chainReact, "ChainCredit",
                "Decentralized credit scoring using multi-chain transaction history.",
                "https://github.com/blockbusters/chaincredit",
                null, 33.5, crSubmitted.minusHours(3)));
        submissionRepository.save(submission(web3Warriors, chainReact, "PeerFund",
                "Peer-to-peer lending marketplace with DAO-governed interest rates.",
                "https://github.com/web3warriors/peerfund",
                "https://peerfund.demo.app", 29.0, crSubmitted.minusHours(5)));

        // ──────────────────────────────────────────
        // 6. PARTICIPANTS
        // ──────────────────────────────────────────
        List<User> allParticipants = List.of(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12);
        List<Hackathon> allHackathons = List.of(hackAI, chainReact, mediHack, greenTech, eduThon);

        // Assign participants to hackathons
        for (User p : List.of(p1, p2, p3, p4, p5, p6, p7, p8)) {
            participantRepository.save(Participants.builder()
                    .user(p).hackathon(hackAI).joinedAt(LocalDateTime.now().minusDays(65)).build());
        }
        for (User p : List.of(p2, p4, p6, p9, p10, p11)) {
            participantRepository.save(Participants.builder()
                    .user(p).hackathon(chainReact).joinedAt(LocalDateTime.now().minusDays(50)).build());
        }
        for (User p : List.of(p8, p9, p10, p11)) {
            participantRepository.save(Participants.builder()
                    .user(p).hackathon(mediHack).joinedAt(LocalDateTime.now().minusDays(6)).build());
        }
        for (User p : List.of(p10, p11, p12)) {
            participantRepository.save(Participants.builder()
                    .user(p).hackathon(greenTech).joinedAt(LocalDateTime.now().minusDays(8)).build());
        }
        for (User p : List.of(p1, p5, p7, p12)) {
            participantRepository.save(Participants.builder()
                    .user(p).hackathon(eduThon).joinedAt(LocalDateTime.now().minusDays(2)).build());
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("status", "success");
        summary.put("users", userRepository.count());
        summary.put("hackathons", hackathonRepository.count());
        summary.put("teams", teamRepository.count());
        summary.put("submissions", submissionRepository.count());
        summary.put("participants", participantRepository.count());
        summary.put("loginEmail", "anika@evnova.dev");
        summary.put("password", "Password123!");
        return ResponseEntity.ok(summary);
    }

    // ──────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────
    private User user(String name, String email, String pwd) {
        return User.builder().name(name).email(email).password(pwd)
                .role(Role.PARTICIPANT).createdAt(LocalDateTime.now().minusDays(30)).build();
    }

    private Team team(String name, Hackathon hackathon, User leader) {
        return Team.builder().name(name).hackathon(hackathon).leader(leader)
                .createdAt(LocalDateTime.now().minusDays(50)).build();
    }

    private void teamMember(Team team, User user, String role) {
        teamMemberRepository.save(TeamMember.builder().team(team).user(user).role(role).build());
    }

    private Submission submission(Team team, Hackathon hackathon, String projectName,
                                   String description, String githubUrl, String demoUrl,
                                   Double score, LocalDateTime submittedAt) {
        return Submission.builder()
                .team(team).hackathon(hackathon).projectName(projectName)
                .description(description).githubUrl(githubUrl).demoUrl(demoUrl)
                .score(score).submittedAt(submittedAt).build();
    }
}
