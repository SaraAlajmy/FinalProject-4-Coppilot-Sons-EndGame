package com.example.e2e.base;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

/**
 * Before this you might need to drop and recreate the database
 * to ensure a clean state. Use ./drop_all_databases.sh from root dir,
 * wait for a few seconds for the pods to be ready, and then run this class.
 * After that, you can import the generated JSON file into Postman.
 * 
 * Generates a JSON file (seed-output.json in root e2e) to be imported into
 * Postman, by creating a dummy request (to google.com for example) and in the pre-request script
 * add this:
 * 
 * var seedOutput = { <Here you embed the output json file content> };
 * 
 * for (let variableName in seedOutput) {
 * pm.environment.set(variableName, seedOutput[variableName])
 * }
 */
@Slf4j
public class Seeder extends BaseApiTest {
    protected Map<String, Map<String, Object>> users = new HashMap<>();
    protected Map<String, String> groups = new HashMap<>();
    protected Map<String, String> chats = new HashMap<>();
    protected Map<String, String> otherVariables = new HashMap<>();

    @Override
    public void login() {
        // No need to login for seeding data
    }

    @Test
    public void seedData() {
        seedUsers();
        muteNotificationsForAllUsers();
        seedDirectMessages();
        seedGroups();
        seedGroupMessages();
        seedSettingsForUsers();
        generateOutputJson();
    }

    public void seedUsers() {
        var userEmails = new HashMap<String, String>();
        userEmails.put("mathew", "mathew.hanybb@gmail.com");
        userEmails.put("nairuz", "nairuzyasser@gmail.com");
        userEmails.put("farah", "farah.alfawzy@gmail.com");
        userEmails.put("dareen", "darin.m.fadel@gmail.com");
        userEmails.put("marwa", "marwaawd5@gmail.com");
        userEmails.put("sara", "saraalagami@gmail.com");
        userEmails.put("hussein", "ah011235813@gmail.com");
        userEmails.put("shorouq", "mathew.hanybb@gmail.com");
        userEmails.put("ziad", "mathew.hanybb@gmail.com");
        userEmails.put("malak", "elwassifmalak@gmail.com");
        userEmails.put("youssef", "yaskm2002@gmail.com");
        userEmails.put("zahra", "zahrasaadawy@gmail.com");
        userEmails.put("ramez", "ramez.nashaat9999@gmail.com");

        for (var entry : userEmails.entrySet()) {
            var username = entry.getKey();
            var email = entry.getValue();
            var user = userTestService.registerUser(generateUser(username, email));
            users.put(username, user);
        }
    }

    public void muteNotificationsForAllUsers() {
        for (var entry : users.entrySet()) {
            var user = entry.getValue();
            log.info("Muting notifications for user: {}", user);
            loggedAs(user, () -> {
                notificationTestService.muteAllNotifications();
            });
        }
    }

    public void seedDirectMessages() {
        // Mathew-Nairuz
        sendDM(users.get("mathew"), users.get("nairuz"), "Hello Nairuz");
        sendDM(users.get("nairuz"), users.get("mathew"), "Hello Mathew");
        sendDM(users.get("mathew"), users.get("nairuz"), "Ana 3ayez ageb coffee");
        sendDM(users.get("nairuz"), users.get("mathew"), "Yalla laroma");
        sendDM(users.get("mathew"), users.get("nairuz"), "Letgoooo");

        // Mathew-Farah
        sendDM(users.get("mathew"), users.get("farah"), "Hello mini boss");
        sendDM(users.get("farah"), users.get("mathew"), "😡");
        sendDM(users.get("mathew"), users.get("farah"), "Tb enty msh farah el aslya ba2a");

        // Farah-Nairuz
        sendDM(users.get("farah"), users.get("nairuz"), "Enty msh el maleka");
        sendDM(users.get("nairuz"), users.get("farah"), "La ana el maleka");
        sendDM(users.get("farah"), users.get("nairuz"), "Laaa");
        sendDM(users.get("nairuz"), users.get("farah"), "Laaaaa");

        // Mathew-Dareen
        sendDM(users.get("mathew"), users.get("dareen"), "Ya dareeeeeeeeeen");
        sendDM(users.get("dareen"), users.get("mathew"), "Ya 3m seebny f 7aly");
        sendDM(users.get("mathew"), users.get("dareen"), "Bra7tek ya dareen");
        sendDM(users.get("dareen"), users.get("mathew"), "Hamsa7lak el PR");
        sendDM(users.get("mathew"), users.get("dareen"), "😭");

        // Mathew-Marwa
        sendDM(users.get("mathew"), users.get("marwa"), "Mafeesh 8erek by7teremni f el gam3a ya Marwa");
        sendDM(users.get("marwa"), users.get("mathew"), "Walla yhezak ya 3m Mathew");

        // Mathew-Sara
        var favoritedMessageId = sendDM(users.get("mathew"), users.get("sara"), "Agmad Scrum Master ❤️🔥");
        sendDM(users.get("sara"), users.get("mathew"), "Thank youuuu Mathewww");
        otherVariables.put("favorited_message_id", favoritedMessageId);
        otherVariables.put("favorited_chat_id", chats.get("mathew_sara"));

        loggedAs(users.get("sara"), () -> {
            messageTestService.markAsFavorite(favoritedMessageId);
        });

        // Mathew-Hussein
        sendDM(users.get("mathew"), users.get("hussein"), "Eh ra2yak f el la engabeya");
        sendDM(users.get("hussein"), users.get("mathew"), "7aram");

        // Mathew-Shorouq
        sendDM(users.get("mathew"), users.get("shorouq"), "Helloooooz Shorouq");
        sendDM(users.get("shorouq"), users.get("mathew"), "Helloooooz Mathew");

        // Mathew-Ziad
        sendDM(users.get("mathew"), users.get("ziad"), "3amel eh ya Ziad");
        sendDM(users.get("ziad"), users.get("mathew"), "😶");

        // Mathew-Malak
        sendDM(users.get("mathew"), users.get("malak"), "5ali el mini boss tr7amni");
        sendDM(users.get("malak"), users.get("mathew"), "el mini boss bra7etha");

        // Mathew-Youssef
        sendDM(users.get("mathew"), users.get("youssef"), "a7la msa 3ala el nas el kwayisa");
        sendDM(users.get("youssef"), users.get("mathew"), "a7la msa ya 3m mathew");

        // Mathew-Zahra
        sendDM(users.get("mathew"), users.get("zahra"), "Zahret el team");
        sendDM(users.get("zahra"), users.get("mathew"), "😂😂");

        // Mathew-Ramez
        sendDM(users.get("ramez"), users.get("mathew"), "Tegy m3aya Darmstadt?");
        sendDM(users.get("mathew"), users.get("ramez"), "La");
        sendDM(users.get("ramez"), users.get("mathew"), "😨😨😨");
    }

    public void seedGroups() {
        var allUsersExceptRamez = users.values().stream()
                .filter(user -> !user.get("username").equals("ramez"))
                .toList();

        groups.put(
                "scalable",
                createGroup(
                        users.get("mathew"),
                        "Scalable",
                        "Final Scalable Project Team",
                        allUsersExceptRamez));

        groups.put(
                "ulm_society",
                createGroup(
                        users.get("ramez"),
                        "Ulm Society",
                        "Ulm Society",
                        List.of(users.get("mathew"), users.get("dareen"), users.get("farah"),
                                users.get("nairuz"))));

        groups.put(
                "security",
                createGroup(
                        users.get("dareen"),
                        "Security",
                        "Security Team",
                        List.of(users.get("dareen"), users.get("mathew"), users.get("zahra"), users.get("youssef"))));
    }

    public void seedGroupMessages() {
        // Scalable
        sendGroupMessage(users.get("mathew"), groups.get("scalable"), "Msh 3ayzeen a3mel script?");
        sendGroupMessage(users.get("nairuz"), groups.get("scalable"), "Nooo");
        sendGroupMessage(users.get("farah"), groups.get("scalable"), "Nooo");
        sendGroupMessage(users.get("dareen"), groups.get("scalable"), "Nooo");
        sendGroupMessage(users.get("marwa"), groups.get("scalable"), "Nooo");
        sendGroupMessage(users.get("sara"), groups.get("scalable"), "Nooo");
        sendGroupMessage(users.get("hussein"), groups.get("scalable"), "Ok");
        sendGroupMessage(users.get("mathew"), groups.get("scalable"), "Tayeb 7d y accept el PR tayeb");
        sendGroupMessage(users.get("dareen"), groups.get("scalable"), "7d yshelo mn el group");
        sendGroupMessage(users.get("mathew"), groups.get("scalable"), "letsgooooo");
        var archivedMessageId = sendGroupMessage(users.get("nairuz"), groups.get("scalable"), "e3mlelo archive tayeb");
        otherVariables.put("archived_message_id", archivedMessageId);
        otherVariables.put("archived_group_id", groups.get("scalable"));

        loggedAs(users.get("sara"), () -> {
            groupMessageTestService.archiveGroupMessage(archivedMessageId);
        });

        // Ulm Society
        sendGroupMessage(users.get("ramez"), groups.get("ulm_society"), "Yalla danube");
        sendGroupMessage(users.get("mathew"), groups.get("ulm_society"), "Letsgoooo");
        sendGroupMessage(users.get("dareen"), groups.get("ulm_society"), "@mathew m3ak nos euro");
        sendGroupMessage(users.get("mathew"), groups.get("ulm_society"), "@dareen la msh m3aya ansass");
        sendGroupMessage(users.get("nairuz"), groups.get("ulm_society"), "@ramez mokta2eb leh");
        sendGroupMessage(users.get("ramez"), groups.get("ulm_society"), "3m brandon t3abni");
        sendGroupMessage(users.get("ramez"), groups.get("ulm_society"), "aw ana t3abto msh 3aref");

        // Security
        sendGroupMessage(users.get("dareen"), groups.get("security"), "Fadyeen emta n3ml el project?");
        sendGroupMessage(users.get("mathew"), groups.get("security"), "Msh fadyeen");
        sendGroupMessage(users.get("youssef"), groups.get("security"), "Ay wa2t ana innnn");
        sendGroupMessage(users.get("dareen"), groups.get("security"), "@mathew 😡");
    }

    public void seedSettingsForUsers() {
        //
    }

    public Map<String, Object> generateUser(String username, String email) {
        return Map.of(
                "username", username,
                "email", email,
                "password", "password",
                "phoneNumber", faker.phoneNumber().phoneNumber());
    }

    public String sendDM(Map<String, Object> sender, Map<String, Object> to, String message) {
        var map = new Map[1];

        loggedAs(sender, () -> {
            map[0] = messageTestService.sendDirectMessage((String) to.get("id"), message);
        });

        var fromUsername = (String) sender.get("username");
        var toUsername = (String) to.get("username");
        var chatId = map[0].get("chatId").toString();

        chats.putIfAbsent(fromUsername + "_" + toUsername, chatId);
        chats.putIfAbsent(toUsername + "_" + fromUsername, chatId);

        return map[0].get("id").toString();
    }

    public String createGroup(
            Map<String, Object> creator,
            String groupName,
            String groupDescription,
            List<Map<String, Object>> members) {
        var map = new Map[1];

        loggedAs(creator, () -> {
            map[0] = groupChatTestService.createGroupChat(
                    groupName,
                    groupDescription,
                    members.stream()
                            .map(m -> (String) m.get("id"))
                            .toList());
        });

        return map[0].get("id").toString();
    }

    public String sendGroupMessage(Map<String, Object> sender, String groupId, String message) {
        var map = new Map[1];

        loggedAs(sender, () -> {
            map[0] = groupMessageTestService.sendGroupMessage(groupId, message);
        });

        return map[0].get("id").toString();
    }

    private void generateOutputJson() {
        var out = new LinkedHashMap<String, Object>();

        users.forEach((k, v) -> out.put(k + "_id", v.get("id")));
        groups.forEach((k, v) -> out.put(k + "_id", v));
        chats.forEach((k, v) -> out.put(k + "_id", v));
        otherVariables.forEach((k, v) -> out.put(k, v));

        var mapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
        Path path = Paths.get("seed-output.json");
        try {
            mapper.writeValue(path.toFile(), out);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to write seed-output.json", e);
        }
        log.info("Wrote seed-output.json → {}", path.toAbsolutePath());
    }
}
