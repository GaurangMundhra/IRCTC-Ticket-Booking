package com.ticketbooking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketbooking.entities.Train;
import com.ticketbooking.entities.User;
import com.ticketbooking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserBookingService {

    private User user;

    private List<User> userList;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String USERS_PATH = "C:/Users/gaura/OneDrive/Desktop/IRCTC/app/src/main/java/com/ticketbooking/localDb/users.json";

    public UserBookingService(User user) throws IOException {
        this.user = user;
        loadUserListFromFile();
    }

    public UserBookingService() throws IOException {
        loadUserListFromFile();
    }

    private void loadUserListFromFile() throws IOException {

        userList = objectMapper.readValue(new File(USERS_PATH), new TypeReference<List<User>>() {});
    }

    public Boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(), user1.getHashPassword());
        }).findFirst();
        return foundUser.isPresent();
    }

    public Boolean signUp(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }

    private void saveUserListToFile() throws IOException
    {
        File userFile = new File(USERS_PATH);
        objectMapper.writeValue(userFile,userList);
    }

    public void fetchBookings(){
        user.printTickets();
    }

    public Boolean cancelBooking(String ticketId){

        Scanner s = new Scanner(System.in);
        System.out.println("Enter the ticket to cancel:");
        ticketId = s.next();

        if(ticketId == null || ticketId.isEmpty()){
            System.out.println("TicketId cannot be null or empty");
            return Boolean.FALSE;
        }

        String finalticketId = ticketId;
        boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equals(finalticketId));

        if(removed){
            System.out.println("Ticket with Id" + ticketId + "has been cancelled");
            return Boolean.TRUE;
        }
        else {
            System.out.println("No ticket found with Id" + ticketId);
            return Boolean.FALSE;
        }
    }

    public List<Train> getTrains(String source, String destination){
        try{
            TrainService trainService = new TrainService();
            return trainService.searchTrains(source, destination);
        }catch (IOException ex){
            return new ArrayList<>();
        }

    }

    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }

    public Boolean bookTrainSeat(Train train, int row, int seat) {
        try{
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = train.getSeats();
            if (row >= 0 && row < seats.size() && seat >= 0 && seat < seats.get(row).size()) {
                if (seats.get(row).get(seat) == 0) {
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);
                    trainService.addTrain(train);
                    return true; // Booking successful
                } else {
                    return false; // Seat is already booked
                }
            } else {
                return false; // Invalid row or seat index
            }
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }

}
