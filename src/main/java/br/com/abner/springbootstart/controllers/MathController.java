package br.com.abner.springbootstart.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.abner.springbootstart.converters.NumberConverter;
import br.com.abner.springbootstart.exceptions.UnsupportedMathOperationException;
import br.com.abner.springbootstart.math.SimpleMath;

@RestController
public class MathController {
    
    SimpleMath math = new SimpleMath();
 
    @RequestMapping(value = "/sum/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double sum(
        @PathVariable(value = "numberOne") String numberOne, 
        @PathVariable(value = "numberTwo") String numberTwo
        ) throws Exception {
            if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)){
                throw new UnsupportedMathOperationException("Please set a numeric value!");
            }
            return math.sum(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping(value = "/subtraction/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double subtraction(
            @PathVariable(value = "numberOne") String numberOne, 
            @PathVariable(value = "numberTwo") String numberTwo
        ) throws Exception {
            if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)){
                throw new UnsupportedMathOperationException("Please set a numeric value!");
            }
            return math.subtraction(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping(value = "/multiplication/{numberOne}/{numberTwo}")
    public Double multiplication(
            @PathVariable(value = "numberOne") String numberOne, 
            @PathVariable(value = "numberTwo") String numberTwo
        ) throws Exception {
            if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)){
                throw new UnsupportedMathOperationException("Please set a numeric value!");
            }
            return math.multiplication(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping(value = "/division/{numberOne}/{numberTwo}")
    public Double division(
            @PathVariable(value = "numberOne") String numberOne, 
            @PathVariable(value = "numberTwo") String numberTwo
        ) throws Exception {
            if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)){
                throw new UnsupportedMathOperationException("Please set a numeric value!");
            }
            if(NumberConverter.convertToDouble(numberTwo) == 0D){
                throw new UnsupportedMathOperationException("Impossible to divide by zero!");
            }
            return math.division(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping(value = "/mean/{numberOne}/{numberTwo}", method = RequestMethod.GET)
    public Double mean(
            @PathVariable(value = "numberOne") String numberOne, 
            @PathVariable(value = "numberTwo") String numberTwo
        ) throws Exception {
            if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)){
                throw new UnsupportedMathOperationException("Please set a numeric value!");
            }
            return math.mean(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping(value = "/sqrt/{number}", method = RequestMethod.GET)
    public Double sqrt(
            @PathVariable(value = "number") String number
        ) throws Exception {
            if(!NumberConverter.isNumeric(number)){
                throw new UnsupportedMathOperationException("Please set a numeric value!");
            }
            return math.sqrt(NumberConverter.convertToDouble(number));
    }


}
