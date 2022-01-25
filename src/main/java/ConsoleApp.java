import dao.*;
import models.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class ConsoleApp {
    private static final DAO<Parent> parentDAO = new ParentDAO();
    private static final DAO<Child> childDAO = new ChildDAO();
    private static final DAO<Address> addressDAO = new AddressDAO();
    private static final DAO<EducationalInstitution> educationalInstitutionDAO = new EducationalInstitutionDAO();
    private static final DAO<District> districtDAO = new DAO<District>() {
        @Override
        public District findEntityById(int id) {
            return querySingleEntity(District.class, id);
        }

        @Override
        public List<District> getAllEntities() {
            return queryAllEntities(District.class);
        }
    };


    public static void main(String[] args) {
        if (args.length == 0) fillData();
        Map<String, Map<String, Consumer<String[]>>> argMap = new HashMap<>();
        argMap.put("add", new HashMap<>());
        argMap.get("add").put("child", ConsoleApp::addChild);
        argMap.get("add").put("parent", ConsoleApp::addParent);
        argMap.put("change", new HashMap<>());
        argMap.get("change").put("educational", ConsoleApp::changeEducationalInstitutionAddress);
        argMap.get("change").put("parent", ConsoleApp::changeParentAddress);
        argMap.get("change").put("child", ConsoleApp::changeEducationalInstitutionForChild);
        String action = "none";
        int i = 0;
        for (; i < args.length; i++) {
            String trimmed = args[i].trim();
            if (argMap.containsKey(trimmed)) {
                action = trimmed;
                break;
            }
        }
        if (action.equals("none")) return;
        for (i = i + 1; i < args.length; i++) {
            if (argMap.get(action).containsKey(args[i].trim())) {
                argMap.get(action).get(args[i].trim()).accept(Arrays.copyOfRange(args, i + 1, args.length));
                break;
            }
        }
    }

    private static void changeEducationalInstitutionForChild(String[] args) {
        try {
            if (!args[0].trim().equals("educational")) {
                System.out.println("usage change child educational child_id educational_institution_id");
                System.out.println("only educational institution can be changed for child");
                return;
            }
            int childId = Integer.parseInt(args[1]);
            int educationalInstitutionId = Integer.parseInt(args[2]);
            childDAO.updateEntityField(childId, educationalInstitutionDAO.findEntityById(educationalInstitutionId), Child::setEducationalInstitution);
            System.out.println("educational institution changed");
        } catch (Exception e) {
            System.out.println("usage change child educational child_id educational_institution_id");
        }
    }

    private static void findEducationalInstitutionsForChild(Child child) {
        List<String> addresses = child
                .getParents()
                .stream()
                .map(Parent::getAddress)
                .map(Address::getDistrict)
                .map(District::getName)
                .collect(Collectors.toList());
        List<EducationalInstitution> educationalInstitutions = educationalInstitutionDAO
                .getAllEntities()
                .stream()
                .filter((ei) -> ei.getAddress() != null && addresses.contains(ei.getAddress().getDistrict().getName()))
                .collect(Collectors.toList());
        educationalInstitutions.forEach(ei -> System.out.println("Подходящее учебное учреждение: id " + ei.getId() + "Номер " + ei.getNumber() + ". адрес: " + ei.getAddress().getAddress()));
    }

    private static void addChild(String[] args) {
        if (args.length < 2) {
            System.out.println("add child arguments: name age [parent_id] [parent_id]");
            return;
        }
        try {
            String name = args[0].toLowerCase(Locale.ROOT);
            int age = Integer.parseInt(args[1]);
            Child child = new Child(name, age);
            if (args.length >= 3) {
                int firstParentId = Integer.parseInt(args[2]);
                child.getParents().add(parentDAO.findEntityById(firstParentId));
            }
            if (args.length >= 4) {
                int secondParentId = Integer.parseInt(args[3]);
                child.getParents().add(parentDAO.findEntityById(secondParentId));
            }
            if (child.getParents() != null && !child.getParents().isEmpty()) {
                findEducationalInstitutionsForChild(child);
            }
            childDAO.save(child);
            System.out.println("child added");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("add child arguments: name age parent_id [parent_id]");
        }
    }

    private static void addParent(String[] args) {
        for (String arg : args) {
            System.out.println(arg);
        }
        if (args.length < 3) {
            System.out.println("add parent arguments: name address district [child_id]");
            return;
        }
        try {
            String name = args[0].trim().toLowerCase(Locale.ROOT);
            String addressName = args[1].trim().toLowerCase(Locale.ROOT);
            String districtName = args[2].trim().toLowerCase(Locale.ROOT);
            District district = districtDAO.findEntityByFieldValue(
                    d -> d.getName().equals(districtName),
                    () -> districtDAO.saveAndGetEntity(new District(districtName)));
            Address address = addressDAO.findEntityByFieldValue(
                    a -> a.getAddress().equals(addressName),
                    () -> addressDAO.saveAndGetEntity(new Address(district, addressName)));
            Parent parent = new Parent(name, address);
            if (args.length >= 4) {
                int childId = Integer.parseInt(args[3]);
                Child childEntity = childDAO.findEntityById(childId);
                parent.getChildren().add(childEntity);
            }
            parentDAO.save(parent);
            System.out.println("parent added");
        } catch (Exception e) {
            System.out.println("add parent arguments: name address district [child_id]");
        }
    }

    private static void changeParentAddress(String[] args) {
        try {
            if (!args[0].trim().equals("address")) {
                System.out.println("usage change parent address parent_id address district");
                System.out.println("only address can be changed for parent");
                return;
            }
            int parentId = Integer.parseInt(args[1]);
            String addressName = args[2].trim().toLowerCase(Locale.ROOT);
            Address address = addressDAO.findEntityByFieldValue(
                    a -> addressName.equals(a.getAddress()),
                    () -> addNewAddressAndDistrict(args[3].trim().toLowerCase(Locale.ROOT), addressName));
            parentDAO.updateEntityField(parentId, address, Parent::setAddress);
            System.out.println("address changed");
        } catch (Exception e) {
            System.out.println("usage change parent address parent_id address_name");
        }
    }

    private static void changeEducationalInstitutionAddress(String[] args) {
        try {
            if (!args[0].trim().equals("address")) {
                System.out.println("usage change educational address educational_institution_id address district");
                System.out.println("only address can be changed for parent");
                return;
            }
            int educationalInstitutionId = Integer.parseInt(args[1]);
            String addressName = args[2].trim().toLowerCase(Locale.ROOT);
            Address address = addressDAO.findEntityByFieldValue(
                    a -> addressName.equals(a.getAddress()),
                    () -> addNewAddressAndDistrict(args[3].trim().toLowerCase(Locale.ROOT), addressName));
            educationalInstitutionDAO.updateEntityField(
                    educationalInstitutionId,
                    address,
                    EducationalInstitution::setAddress);
            System.out.println("address changed");
        } catch (Exception e) {
            System.out.println("usage change parent address address_name parent_id");
        }
    }

    private static Address addNewAddressAndDistrict(String districtName, String address) {
        District district = districtDAO.findEntityByFieldValue(
                d -> d.getName().toLowerCase(Locale.ROOT).equals(districtName),
                () -> {
                    District districtEntity = new District(districtName);
                    districtDAO.save(districtEntity);
                    return districtEntity;
                });
        return new Address(district, address);
    }

    private static void fillData() {
        List<District> districts = Arrays.asList(
                new District("december district"),
                new District("stalin district")
        );
        for (District district : districts) {
            districtDAO.save(district);
        }
        List<Address> addresses = Arrays.asList(
                new Address(districts.get(0), "first street,29"),
                new Address(districts.get(1), "second street,5"),
                new Address(districts.get(0), "third street,25"),
                new Address(districts.get(1), "fourth street,23"),
                new Address(districts.get(0), "fifth street,17"),
                new Address(districts.get(1), "sixth street,6"),
                new Address(districts.get(0), "seventh street,1"),
                new Address(districts.get(1), "eighth street,8")
        );
        //у меня с кодировками плохо, поэтому всё на английском.
        addresses.forEach(addressDAO::save);

        for (int i = 0; i < 6; i++) {
            EducationalInstitution school = new EducationalInstitution(addresses.get(i), i);
            educationalInstitutionDAO.save(school);
        }
    }
}
