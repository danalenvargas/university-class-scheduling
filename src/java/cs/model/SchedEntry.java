/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cs.model;

import java.util.Date;

/**
 *
 * @author Dan
 */
public class SchedEntry {

    private int schedID, start, end;
    private int sectionId, instructorId, roomId, subjectId;
    private Instructor instructor;
    private Room room;
    private Subject subject;
    private Section section;
    private Date timestamp; 
    private String status;
    private User creator;
    private boolean isReturnRequested;
            
    public SchedEntry() {
    }

    public SchedEntry(int schedID, int start, int end, int instructorId, int roomId, int subjectId, int sectionId) {
        this.schedID = schedID;
        this.start = start;
        this.end = end;
        this.sectionId = sectionId;
        this.instructorId = instructorId;
        this.roomId = roomId;
        this.subjectId = subjectId;
    }
    
    public SchedEntry(int schedID, int start, int end, Instructor instructor, Room room, int subjectId, Section section) {

        this.schedID = schedID;
        this.start = start;
        this.end = end;
        this.instructor = instructor;
        this.room = room;
        this.subjectId = subjectId;
        this.section = section;
    }
    
    // v TEMPORARY, TO DELETE
    public SchedEntry(int schedID, int start, int end, Instructor instructor, Room room, Subject subject, int sectionId) {
        this.schedID = schedID;
        this.start = start;
        this.end = end;
        this.instructor = instructor;
        this.room = room;
        this.subject = subject;
        this.sectionId = sectionId;
    }
    // ^ TEMPORARY, TO DELETE
    
    public SchedEntry(int schedID, int start, int end, Instructor instructor, Room room, Subject subject) {
        this.schedID = schedID;
        this.start = start;
        this.end = end;
        this.instructor = instructor;
        this.room = room;
        this.subject = subject;
    }

    public SchedEntry(int schedID, int start, int end, Instructor instructor, Room room, Subject subject, Section section) {
        this.schedID = schedID;
        this.start = start;
        this.end = end;
        this.instructor = instructor;
        this.room = room;
        this.subject = subject;
        this.section = section;
    }

    public SchedEntry(int schedID, int start, int end, Instructor instructor, Room room, Subject subject, Section section, String status, User creator) {
        this.schedID = schedID;
        this.start = start;
        this.end = end;
        this.instructor = instructor;
        this.room = room;
        this.subject = subject;
        this.section = section;
        this.status = status;
        this.creator = creator;
    }
    
    public SchedEntry(int schedID, int start, int end, Instructor instructor, Room room, Subject subject, Section section, String status, User creator, Date timestamp) {
        this.schedID = schedID;
        this.start = start;
        this.end = end;
        this.instructor = instructor;
        this.room = room;
        this.subject = subject;
        this.section = section;
        this.status = status;
        this.creator = creator;
        this.timestamp = timestamp;
    }
    
    public SchedEntry(int schedID, int start, int end, Instructor instructor, Room room, Subject subject, Section section, String status, User creator, Date timestamp, boolean isReturnRequested) {
        this.schedID = schedID;
        this.start = start;
        this.end = end;
        this.instructor = instructor;
        this.room = room;
        this.subject = subject;
        this.section = section;
        this.status = status;
        this.creator = creator;
        this.timestamp = timestamp;
        this.isReturnRequested = isReturnRequested;
    }
    
    public SchedEntry(int schedID, int start, int end, Instructor instructor, Room room, Subject subject, Section section, Date timestamp) {
        this.schedID = schedID;
        this.start = start;
        this.end = end;
        this.instructor = instructor;
        this.room = room;
        this.subject = subject;
        this.section = section;
        this.timestamp = timestamp;
    }

    public int getSchedID() {
        return schedID;
    }

    public void setSchedID(int schedID) {
        this.schedID = schedID;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
    
    public float getTimeSpan(){ // returns time span in hours
        return (end - start)/2;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    

    public boolean isSimilar(SchedEntry that) {
        if(this.subject!=null && that.subject!= null){
            return this.section.getSectionID() == that.section.getSectionID()
                    && this.subject.getSubjectID() == that.subject.getSubjectID()
                    && this.instructor.getInstructorID() == that.instructor.getInstructorID()
                    && this.room.getRoomID() == that.room.getRoomID();
        }else{
            return this.section.getSectionID() == that.section.getSectionID()
                    && this.getSubjectId() == that.getSubjectId()
                    && this.instructor.getInstructorID() == that.instructor.getInstructorID()
                    && this.room.getRoomID() == that.room.getRoomID();
        }
    }
}
