package com.example.rentalService.web;

import com.example.rentalService.data.Car;
import com.example.rentalService.data.Dates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;

@RestController
public class RentalWebService {

    List<Car> cars = new ArrayList<Car>();

    Logger logger = LoggerFactory.getLogger(RentalWebService.class);

    public RentalWebService() {
        Car car = new Car("11AA22", 2000);
        cars.add(car);
        car = new Car("22BB33", 3000);
        cars.add(car);
        car = new Car("33CC44", 6000);
        cars.add(car);
        car = new Car("44DD55", 5000);
        cars.add(car);
        car = new Car("55EE66", 4500);
        cars.add(car);
        car = new Car("66FF77", 5000);
        cars.add(car);
    }

    @GetMapping("/cars")
    public List<Car> getCars(){
        return cars.stream().filter(car -> !car.isRented()).toList(); // Filtrer les voitures non louées
    }

    @PutMapping(value = "/cars/{plaque}")
    public void rent(
            @PathVariable("plaque") String plateNumber,
            @RequestParam(value="rent", required = true)boolean rent) throws CarNotFoundException {
        LocalDate dates = LocalDate.now();
        logger.info("Plate number: " + plateNumber);
        logger.info("Rent: " + rent);

        Car car = cars.stream().filter(aCar -> aCar.getPlateNumber().equals(plateNumber)).findFirst().orElse(null);
        if(car != null){
            Dates dateRent = new Dates();
            if(rent == true){
                car.setRented(true);
                dateRent.setBegin(""+dates);
                dateRent.setEnd("null");
            } else {
                car.setRented(false);
                dateRent = car.getDates().getLast();
                dateRent.setEnd(""+dates);
                car.getDates().removeLast();
            }
            car.getDates().add(dateRent);
        } else {
            logger.error("Car not found: " + plateNumber);
            throw new CarNotFoundException(plateNumber);
        }

    }
    @GetMapping("/cars/sorted")
    public List<Car> getAvailableCarsSortedByPrice() {
        return cars.stream()
                .filter(car -> !car.isRented()) // Filtrer les voitures non louées
                .sorted(Comparator.comparingInt(Car::getPrice)) // Trier par prix croissant
                .toList();
    }

}