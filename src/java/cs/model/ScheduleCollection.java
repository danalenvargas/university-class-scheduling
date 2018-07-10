/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.model;

/**
 *
 * @author Dan
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class ScheduleCollection {

    int sem = 2;//temporary variable initialization

    private HashMap<Integer, CollegeElement> collegeCollection;

    public ScheduleCollection() {
        this.collegeCollection = new HashMap<>();
    }

    private void addCollege(int collegeID) {
        collegeCollection.put(collegeID, new CollegeElement());
    }

    class CollegeElement {

        private HashMap<Integer, DepartmentElement> departmentCollection;

        CollegeElement() {
            this.departmentCollection = new HashMap<>();
        }

        private void addDepartment(int departmentID) {
            departmentCollection.put(departmentID, new DepartmentElement());
        }
    }

    class DepartmentElement {

        HashMap<Integer, EntryElement> sectionCollection;
        HashMap<Integer, EntryElement> roomCollection;
        HashMap<Integer, EntryElement> instructorCollection;

        DepartmentElement() {
            this.sectionCollection = new HashMap<>();
            this.roomCollection = new HashMap<>();
            this.instructorCollection = new HashMap<>();
        }

        private void addSection(EntryElement section) {
            section.getFreeTree().put(new Interval(0, 120), null);
            this.sectionCollection.put(section.id, section);
        }

        private void addInstructor(EntryElement instructor) {
            instructor.getFreeTree().put(new Interval(0, 120), null);
            this.instructorCollection.put(instructor.id, instructor);
        }

        private void addRoom(EntryElement room) {
            room.getFreeTree().put(new Interval(0, 120), null);
            this.roomCollection.put(room.id, room);
        }

        private void removeSection(int sectionId) {
            this.sectionCollection.remove(sectionId);
        }

        private void removeRoom(int roomId) {
            this.roomCollection.remove(roomId);
        }

        private void removeInstructor(int instructorId) {
            this.instructorCollection.remove(instructorId);
        }
    }

    public void addSectionSchedule(SchedEntry schedEntry) {
        int sectionID = schedEntry.getSection().getSectionID();
        //int sectionID = schedEntry.getSectionId();
        int departmentID = schedEntry.getSection().getDepartmentID();
        int collegeID = schedEntry.getSection().getCollegeID();

        DepartmentElement d = findDepartmentElement(collegeID, departmentID);
        EntryElement section = d.sectionCollection.get(sectionID);

        Interval interval = new Interval(schedEntry.getStart(), schedEntry.getEnd());
        section.getUsedTree().put(interval, schedEntry);
        section.getFreeTree().remove(interval);

        //modify hours plotted on section's subject list
        int subjectId = schedEntry.getSubjectId();
        System.out.println("calculating TIME: " + schedEntry.getEnd() + " " + schedEntry.getStart());
        float end = schedEntry.getEnd();
        float start = schedEntry.getStart();
//        float time = (schedEntry.getEnd() - schedEntry.getStart()) / 2;
        float time = (end - start) / 2;
        System.out.println("TIME: " + time);
//        //
//        System.out.println("------------------------------ INSIDE ADD SECTION -------------------");
//        System.out.println(section.getName());
//        System.out.println(section.getCourse().getCode());
//        System.out.println(section.getCourse().getCurriculum());
////        System.out.println(section.getCourse().getCurriculum().get(sem));
//        System.out.println(",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,");
//        //
        Float oldTime = section.plottedHoursList.get(subjectId);
        if (oldTime != null) {
            section.plottedHoursList.put(subjectId, time + oldTime);
        } else {
            section.plottedHoursList.put(subjectId, time);
        }
        //Subject subject = section.getCourse().getCurriculum().get(sem).get(subjectId);
        //subject.setHoursPlotted(subject.getHoursPlotted() + time);
    }

    public void addInstructorSchedule(SchedEntry schedEntry) {
        if (schedEntry.getInstructor() != null) {
            System.out.println("SCHEDENTRY INSTRUCTOR NOT NULL");
            int instructorID = schedEntry.getInstructor().getInstructorID();
            int departmentID = schedEntry.getInstructor().getDepartmentID();
            int collegeID = schedEntry.getInstructor().getCollegeID();

            DepartmentElement d = findDepartmentElement(collegeID, departmentID);
            EntryElement instructor = d.instructorCollection.get(instructorID);

            Interval interval = new Interval(schedEntry.getStart(), schedEntry.getEnd());
            instructor.getUsedTree().put(interval, schedEntry);
            instructor.getFreeTree().remove(interval);
        }
    }

    public void addRoomSchedule(SchedEntry schedEntry) {

        if (schedEntry.getRoom() != null) {
            int roomID = schedEntry.getRoom().getRoomID();
            int departmentID = schedEntry.getRoom().getDepartmentID();
            int collegeID = schedEntry.getRoom().getCollegeID();

            DepartmentElement d = findDepartmentElement(collegeID, departmentID);
            EntryElement room = d.roomCollection.get(roomID);

            Interval interval = new Interval(schedEntry.getStart(), schedEntry.getEnd());
            room.getUsedTree().put(interval, schedEntry);
            room.getFreeTree().remove(interval);
        }
//        int roomID = schedEntry.getRoom() != null ? schedEntry.getRoom().getRoomID() : null;
    }

    public void removeSectionSchedule(SchedEntry schedEntry) {
        System.out.println("inside removeSectionSchedule");
        int sectionID = schedEntry.getSection().getSectionID();
        int departmentID = schedEntry.getSection().getCourse().getDepartment().getDepartmentID();
        int collegeID = schedEntry.getSection().getCourse().getDepartment().getCollege().getCollegeID();

        DepartmentElement d = findDepartmentElement(collegeID, departmentID);
        EntryElement section = d.sectionCollection.get(sectionID);

        Interval interval = new Interval(schedEntry.getStart(), schedEntry.getEnd());
        section.getUsedTree().remove(interval);
        section.getFreeTree().put(interval, null);
        //modify hours plotted on section's subject list
        int subjectId = schedEntry.getSubject().getSubjectID();
        float end = schedEntry.getEnd();
        float start = schedEntry.getStart();
        float time = (end - start) / 2;
        System.out.println("time: " + time);
        //Subject subject = section.getCourse().getCurriculum().get(sem).get(subjectId);
        //subject.setHoursPlotted(subject.getHoursPlotted() - time);
        Float oldTime = section.plottedHoursList.get(subjectId);
        System.out.println("oldTime: " + oldTime);
        if (oldTime - time == 0) {
            section.plottedHoursList.remove(subjectId);
        } else {
            section.plottedHoursList.put(subjectId, oldTime - time);
        }
        System.out.println("time in plottedHoursList: " + section.plottedHoursList.get(subjectId));
    }

    public void removeInstructorSchedule(SchedEntry schedEntry) {
        if (schedEntry.getInstructor() != null) {
            int instructorID = schedEntry.getInstructor().getInstructorID();
            int departmentID = schedEntry.getInstructor().getDepartment().getDepartmentID();
            int collegeID = schedEntry.getInstructor().getDepartment().getCollege().getCollegeID();

            DepartmentElement d = findDepartmentElement(collegeID, departmentID);
            EntryElement instructor = d.instructorCollection.get(instructorID);

            Interval interval = new Interval(schedEntry.getStart(), schedEntry.getEnd());
            instructor.getUsedTree().remove(interval);
            instructor.getFreeTree().put(interval, null);
        }
    }

    public void removeRoomSchedule(SchedEntry schedEntry) {

        if (schedEntry.getRoom() != null) {
            int roomID = schedEntry.getRoom().getRoomID();
            int departmentID = schedEntry.getRoom().getDepartment().getDepartmentID();
            int collegeID = schedEntry.getRoom().getDepartment().getCollege().getCollegeID();

            DepartmentElement d = findDepartmentElement(collegeID, departmentID);
            EntryElement room = d.roomCollection.get(roomID);

            Interval interval = new Interval(schedEntry.getStart(), schedEntry.getEnd());
            room.getUsedTree().remove(interval);
            room.getFreeTree().put(interval, null);
        }
//        int roomID = schedEntry.getRoom() != null ? schedEntry.getRoom().getRoomID() : null;
    }

    public void addSchedule(SchedEntry schedEntry) {
        addSectionSchedule(schedEntry);
        addRoomSchedule(schedEntry);
        addInstructorSchedule(schedEntry);
    }

    public void removeSchedule(SchedEntry schedEntry) {
        removeSectionSchedule(schedEntry);
        removeRoomSchedule(schedEntry);
        removeInstructorSchedule(schedEntry);
    }

    public DepartmentElement findDepartmentElement(int collegeID, int departmentID) {
        CollegeElement c = collegeCollection.get(collegeID);
        if (c == null) {
            addCollege(collegeID);
            c = collegeCollection.get(collegeID);
        }

        DepartmentElement d = c.departmentCollection.get(departmentID);
        if (d == null) {
            c.addDepartment(departmentID);
            d = c.departmentCollection.get(departmentID);
        }

        return d;
    }

    public void addSection(int sectionId, int departmentId, int collegeId) {
        int collegeID = collegeId;
        int departmentID = departmentId;
        DepartmentElement d = findDepartmentElement(collegeID, departmentID);
        EntryElement section = new EntryElement(sectionId, new HashMap<>());
        d.addSection(section);
    }

    public void removeSection(int sectionId, int departmentId, int collegeId) {
        DepartmentElement d = findDepartmentElement(collegeId, departmentId);
        d.removeSection(sectionId);
        System.out.println("section removed from IST");
    }

    public void removeRoom(int roomId, int departmentId, int collegeId) {
        DepartmentElement d = findDepartmentElement(collegeId, departmentId);
        d.removeRoom(roomId);
        System.out.println("room removed from IST");
    }

    public void removeInstructor(int instructorId, int departmentId, int collegeId) {
        DepartmentElement d = findDepartmentElement(collegeId, departmentId);
        d.removeInstructor(instructorId);
        System.out.println("instructor removed from IST");
    }

    public void addRoom(int roomId, int departmentId, int collegeId) {
        int collegeID = collegeId;
        int departmentID = departmentId;
        DepartmentElement d = findDepartmentElement(collegeID, departmentID);
        EntryElement room = new EntryElement(roomId);
        d.addRoom(room);
    }

    public void addInstructor(int instructorId, int departmentId, int collegeId) {
        int collegeID = collegeId;
        int departmentID = departmentId;
        DepartmentElement d = findDepartmentElement(collegeID, departmentID);
        EntryElement instructor = new EntryElement(instructorId);
        d.addInstructor(instructor);
    }

    public IntervalST<SchedEntry> getUsedTree(String type, int collegeID, int departmentID, int itemId) {

        System.out.println("getting used tree: " + collegeID + " " + departmentID + " " + itemId);
        System.out.println("***** inside get tree");
        CollegeElement c = collegeCollection.get(collegeID);
        System.out.println("***** got college");
        DepartmentElement d = c.departmentCollection.get(departmentID);
        System.out.println("***** got dept");
        IntervalST<SchedEntry> schedTree = null;
        switch (type) {
            case "section":
                schedTree = d.sectionCollection.get(itemId).getUsedTree();
                break;
            case "instructor":
                schedTree = d.instructorCollection.get(itemId).getUsedTree();
                break;
            case "room":
                schedTree = d.roomCollection.get(itemId).getUsedTree();
                break;
        }
        return schedTree;
    }

    public IntervalST<SchedEntry> getFreeTree(String type, int collegeID, int departmentID, int itemId) {
        CollegeElement c = collegeCollection.get(collegeID);
        DepartmentElement d = c.departmentCollection.get(departmentID);
        IntervalST<SchedEntry> schedTree = null;
        switch (type) {
            case "section":
                schedTree = d.sectionCollection.get(itemId).getFreeTree();
                break;
            case "instructor":
                schedTree = d.instructorCollection.get(itemId).getFreeTree();
                break;
            case "room":
                schedTree = d.roomCollection.get(itemId).getFreeTree();
                break;
        }
        return schedTree;
    }

    /* FOR GENERATE SELECTION LIST
     **
     **
     */
    public static class SelectionEntry {

        String name, groupName; // groupName is for the grouping in the selection box, format- section:[CollegeCode_DepartmentCode_Course] room:[Collegeode_DepartmentCode]
        int id, collegeId, departmentId;
        boolean isAssigned;

//        public SelectionEntry(int id, String name, int collegeId, int departmentId, String collegeCode, String departmentCode, String courseCode) { // for Section
//            this.name = name;
//            this.id = id;
//            this.collegeId = collegeId;
//            this.departmentId = departmentId;
//            this.groupName = collegeCode + "_" + departmentCode + "_" + courseCode;
//        }
        
        public SelectionEntry(int id, String name, int collegeId, int departmentId, String collegeCode, String departmentCode, String courseCode, boolean isAssigned) { // for Section
            this.name = name;
            this.id = id;
            this.collegeId = collegeId;
            this.departmentId = departmentId;
            this.groupName = collegeCode + "_" + departmentCode + "_" + courseCode;
            this.isAssigned = isAssigned;
        }

        public SelectionEntry(int id, String name, int collegeId, int departmentId, String collegeCode, String departmentCode) { // for Room and instructors
            this.name = name;
            this.id = id;
            this.collegeId = collegeId;
            this.departmentId = departmentId;
            this.groupName = collegeCode + "_" + departmentCode;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof SelectionEntry)) {
                return false;
            }
            SelectionEntry selectionEntry = (SelectionEntry) o;
            return id == selectionEntry.id && collegeId == selectionEntry.collegeId && departmentId == selectionEntry.departmentId &&
                    name.equals(selectionEntry.name) &&
                    groupName.equals(selectionEntry.groupName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, groupName, id, collegeId, departmentId);
        }

    }

    public ArrayList<SelectionEntry> getAllDepartmentSections(int collegeID, int departmentID) {
        //HashMap<Integer, String> sectionList = new HashMap<>();
        ArrayList<SelectionEntry> sectionList = new ArrayList<>();
        int sectionID;
        String sectionName, courseCode, departmentCode, collegeCode;
        DepartmentElement dept = findDepartmentElement(collegeID, departmentID);
// TEMPORARILY COMMENTED OUT
//        for (HashMap.Entry<Integer, Section> entry : dept.sectionCollection.entrySet()) {
//            Section section = entry.getValue();
//            sectionID = section.getSectionID();
//            sectionName = section.getName();
//            courseCode = section.getCourse().getCode();
//            departmentCode = section.getCourse().getDepartment().getCode();
//            collegeCode = section.getCourse().getDepartment().getCollege().getCode();
//
//            sectionList.add(new SelectionEntry(sectionID, sectionName, collegeID, departmentID, collegeCode, departmentCode, courseCode));
//        }
        return sectionList;
    }

//    public ArrayList<SelectionEntry> getAllCollegeSections(int collegeID) {
//        ArrayList<SelectionEntry> sectionList = new ArrayList<>();
//        int sectionID, departmentID;
//        String sectionName;
//        CollegeElement college = collegeCollection.get(collegeID);
//
//        for (HashMap.Entry<Integer, DepartmentElement> deptEntry : college.departmentCollection.entrySet()) {
//            departmentID = deptEntry.getKey();
//            for (HashMap.Entry<Integer, Section> entry : deptEntry.getValue().sectionCollection.entrySet()) {
//                Section section = entry.getValue();
//                sectionID = section.getSectionID();
//                sectionName = section.getName();
//
//                sectionList.add(new SelectionEntry(sectionID, sectionName, departmentID, collegeID));
//            }
//        }
//        return sectionList;
//    }
    public ArrayList<Integer> getAllCollegeSections(int collegeID) {
        ArrayList<Integer> sectionList = new ArrayList<>();
        int sectionID;//, departmentID;
        //String sectionName, courseCode, departmentCode, collegeCode;
        CollegeElement college = collegeCollection.get(collegeID);
        System.out.println("got College Element");
        if (college == null) {
            addCollege(collegeID);
            college = collegeCollection.get(collegeID);
        }
        System.out.println(college);

        for (HashMap.Entry<Integer, DepartmentElement> deptEntry : college.departmentCollection.entrySet()) {
//            departmentID = deptEntry.getKey();
            for (HashMap.Entry<Integer, EntryElement> entry : deptEntry.getValue().sectionCollection.entrySet()) {
                EntryElement section = entry.getValue();
                sectionID = section.id;
//                sectionName = section.getName();
//                courseCode = section.getCourse().getCode();
//                departmentCode = section.getCourse().getDepartment().getCode();
//                collegeCode = section.getCourse().getDepartment().getCollege().getCode();
//
//                sectionList.add(new SelectionEntry(sectionID, sectionName, collegeID, departmentID, collegeCode, departmentCode, courseCode));
                sectionList.add(sectionID);
            }
        }
        return sectionList;
    }

    public ArrayList<SelectionEntry> getAllDepartmentInstructors(int collegeID, int departmentID) {
        ArrayList<SelectionEntry> instructorList = new ArrayList<>();
        int instructorID;
        String instructorName, departmentCode, collegeCode;
        DepartmentElement dept = findDepartmentElement(collegeID, departmentID);
// TEMPORARILY COMMENTED OUT
//        for (HashMap.Entry<Integer, Instructor> entry : dept.instructorCollection.entrySet()) {
//            Instructor instructor = entry.getValue();
//            instructorID = instructor.getInstructorID();
//            instructorName = instructor.getName();
//            departmentCode = instructor.getDepartment().getCode();
//            collegeCode = instructor.getDepartment().getCollege().getCode();
//
//            instructorList.add(new SelectionEntry(instructorID, instructorName, collegeID, departmentID, collegeCode, departmentCode));
//        }
        return instructorList;
    }

    public ArrayList<Integer> getAllCollegeInstructors(int collegeID) {
        ArrayList<Integer> instructorList = new ArrayList<>();
        int instructorID;//, departmentID;
//        String instructorName, departmentCode, collegeCode;
        CollegeElement college = collegeCollection.get(collegeID);

        for (HashMap.Entry<Integer, DepartmentElement> deptEntry : college.departmentCollection.entrySet()) {
//            departmentID = deptEntry.getKey();
            for (HashMap.Entry<Integer, EntryElement> entry : deptEntry.getValue().instructorCollection.entrySet()) {
                EntryElement instructor = entry.getValue();
                instructorID = instructor.id;
//                instructorName = instructor.getName();
//                departmentCode = instructor.getDepartment().getCode();
//                collegeCode = instructor.getDepartment().getCollege().getCode();
//
//                instructorList.add(new SelectionEntry(instructorID, instructorName, collegeID, departmentID, collegeCode, departmentCode));
                instructorList.add(instructorID);
            }
        }
        return instructorList;
    }

    public ArrayList<SelectionEntry> getAllDepartmentRooms(int collegeID, int departmentID) {
        ArrayList<SelectionEntry> roomList = new ArrayList<>();
        int roomID;
        String roomName, departmentCode, collegeCode;
        DepartmentElement dept = findDepartmentElement(collegeID, departmentID);
// TEMPORARILY COMMENTED OUT
//        for (HashMap.Entry<Integer, Room> entry : dept.roomCollection.entrySet()) {
//            Room room = entry.getValue();
//            roomID = room.getRoomID();
//            roomName = room.getName();
//            departmentCode = room.getDepartment().getCode();
//            collegeCode = room.getDepartment().getCollege().getCode();
//
//            roomList.add(new SelectionEntry(roomID, roomName, collegeID, departmentID, collegeCode, departmentCode));
//        }
        return roomList;
    }

    public ArrayList<Integer> getAllCollegeRooms(int collegeID) {
        ArrayList<Integer> roomList = new ArrayList<>();
        int roomID;//, departmentID;
//        String roomName, departmentCode, collegeCode;
        CollegeElement college = collegeCollection.get(collegeID);
        for (HashMap.Entry<Integer, DepartmentElement> deptEntry : college.departmentCollection.entrySet()) {
//            departmentID = deptEntry.getKey();
            for (HashMap.Entry<Integer, EntryElement> entry : deptEntry.getValue().roomCollection.entrySet()) {
                EntryElement room = entry.getValue();
                roomID = room.id;
//                roomName = room.getName();
//                departmentCode = room.getDepartment().getCode();
//                collegeCode = room.getDepartment().getCollege().getCode();
//
//                roomList.add(new SelectionEntry(roomID, roomName, collegeID, departmentID, collegeCode, departmentCode));
                roomList.add(roomID);
            }
        }
        return roomList;
    }

    public ArrayList<Integer> getFreeRooms(int collegeID, int start, int end, int origStart, int origEnd, int origRoomId) {
        ArrayList<Integer> roomList = new ArrayList<>();
        int roomID;
        CollegeElement college = collegeCollection.get(collegeID);
        for (HashMap.Entry<Integer, DepartmentElement> deptEntry : college.departmentCollection.entrySet()) {
            for (HashMap.Entry<Integer, EntryElement> entry : deptEntry.getValue().roomCollection.entrySet()) {
                EntryElement room = entry.getValue();
                if (room.id != origRoomId) {
                    if (room.getFreeTree().searchInclusive(new Interval(start, end))) {
                        roomID = room.id;
                        roomList.add(roomID);
                    } else {
//                        System.out.println("NO match found, NOT adding");
                    }
                } else {
                    boolean free = true;
                    if (start < origStart) {
                        if (!room.getFreeTree().searchInclusive(new Interval(start, origStart))) {
                            free = false;
                        }
                    }
                    if (end > origEnd) {
                        if (!room.getFreeTree().searchInclusive(new Interval(origEnd, end))) {
                            free = false;
                        }
                    }
                    if (free) {
                        roomID = room.id;
                        roomList.add(roomID);
                    }
                }
            }
        }
        return roomList;
    }

    public boolean isRoomFree(int collegeId, int departmentId, int roomId, int start, int end, int origStart, int origEnd, int origRoomId) {
        CollegeElement college = collegeCollection.get(collegeId);
        DepartmentElement department = college.departmentCollection.get(departmentId);
        EntryElement room = department.roomCollection.get(roomId);

        if (room.id != origRoomId) {
            if (room.getFreeTree().searchInclusive(new Interval(start, end))) {
                return true;
            } else {
                return false;
            }
        } else {
            boolean free = true;
            if (start < origStart) {
                if (!room.getFreeTree().searchInclusive(new Interval(start, origStart))) {
                    free = false;
                }
            }
            if (end > origEnd) {
                if (!room.getFreeTree().searchInclusive(new Interval(origEnd, end))) {
                    free = false;
                }
            }
            if (free) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isInstructorFree(int collegeId, int departmentId, int instructorId, int start, int end, int origStart, int origEnd, int origInstructorId) {
        CollegeElement college = collegeCollection.get(collegeId);
        DepartmentElement department = college.departmentCollection.get(departmentId);
        EntryElement instructor = department.instructorCollection.get(instructorId);

        if (instructor.id != origInstructorId) {
            if (instructor.getFreeTree().searchInclusive(new Interval(start, end))) {
                return true;
            } else {
                return false;
            }
        } else {
            boolean free = true;
            if (start < origStart) {
                if (!instructor.getFreeTree().searchInclusive(new Interval(start, origStart))) {
                    free = false;
                }
            }
            if (end > origEnd) {
                if (!instructor.getFreeTree().searchInclusive(new Interval(origEnd, end))) {
                    free = false;
                }
            }
            if (free) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Integer> getFreeInstructors(int collegeID, int start, int end, int origStart, int origEnd, int origInstructor) {
        ArrayList<Integer> instructorList = new ArrayList<>();
        int instructorID;//, departmentID;
//        String instructorName, departmentCode, collegeCode;
        CollegeElement college = collegeCollection.get(collegeID);
        for (HashMap.Entry<Integer, DepartmentElement> deptEntry : college.departmentCollection.entrySet()) {
//            departmentID = deptEntry.getKey();
            for (HashMap.Entry<Integer, EntryElement> entry : deptEntry.getValue().instructorCollection.entrySet()) {
                EntryElement instructor = entry.getValue();
                //v** DEBUG CODE
//                System.out.println("SEARCHING FOR FREE ROOM. start: " + start + ", end: " + end);
//                System.out.println("tree content: " + instructor.getFreeTree());
//                System.out.println("search result: " + instructor.getFreeTree().searchInclusive(new Interval(start, end)));
//                IntervalST st = instructor.getFreeTree();
//                for (Object x : st.searchAll(new Interval(start, end))) {
//                    System.out.print(x + " ");
//                }
                //^** DEBUG CODE
                if (instructor.id != origInstructor) {
                    if (instructor.getFreeTree().searchInclusive(new Interval(start, end))) {
                        System.out.println("match found, adding instructor...");
                        instructorID = instructor.id;
//                        instructorName = instructor.getName();
//                        departmentCode = instructor.getDepartment().getCode();
//                        collegeCode = instructor.getDepartment().getCollege().getCode();
//                        instructorList.add(new SelectionEntry(instructorID, instructorName, collegeID, departmentID, collegeCode, departmentCode));
                        instructorList.add(instructorID);
                    } else {
                        System.out.println("NO match found, NOT adding");
                    }
                } else {
                    boolean free = true;
                    if (start < origStart) {
                        if (!instructor.getFreeTree().searchInclusive(new Interval(start, origStart))) {
                            free = false;
                        }
                    }
                    if (end > origEnd) {
                        if (!instructor.getFreeTree().searchInclusive(new Interval(origEnd, end))) {
                            free = false;
                        }
                    }
                    if (free) {
//                        instructorID = instructor.getInstructorID();
//                        instructorName = instructor.getName();
//                        departmentCode = instructor.getDepartment().getCode();
//                        collegeCode = instructor.getDepartment().getCollege().getCode();
//                        instructorList.add(new SelectionEntry(instructorID, instructorName, collegeID, departmentID, collegeCode, departmentCode));
                        instructorID = instructor.id;
                        instructorList.add(instructorID);
                    }
                }
            }
        }
        return instructorList;
    }

    // v SHOULD BE UNUSED NOW
//    public HashMap<Integer, Subject> getSubjectList(int collegeId, int departmentId, int sectionId, int sem) {
//        DepartmentElement dept = findDepartmentElement(collegeId, departmentId);
//        Section section = dept.sectionCollection.get(sectionId);
//        System.out.println(section.getCourse().getName());
//        return section.getCourse().getCurriculum().get(sem);
//    }
    // ^ SHOULD BE UNUSED NOW
    public float getSubjectPlottedHours(int collegeId, int departmentId, int sectionId, int subjectId) {
        DepartmentElement dept = findDepartmentElement(collegeId, departmentId);
        EntryElement section = dept.sectionCollection.get(sectionId);
        if (section.plottedHoursList.get(subjectId) != null) {
            return section.plottedHoursList.get(subjectId);
        } else {
            return 0;
        }
    }

    class EntryElement extends Schedule {

        int id;
        HashMap<Integer, Float> plottedHoursList; // for sections only, HashMap<subjectId, hrsPlotted>

        EntryElement(int entryId) {
            this.id = entryId;
        }

        EntryElement(int entryId, HashMap<Integer, Float> plottedHoursList) {
            this.id = entryId;
            this.plottedHoursList = plottedHoursList;
        }
    }
}
