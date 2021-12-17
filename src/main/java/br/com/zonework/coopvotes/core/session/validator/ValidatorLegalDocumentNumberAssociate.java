package br.com.zonework.coopvotes.core.session.validator;

import static br.com.zonework.coopvotes.structure.data.MessageMapper.LEGAL_DOCUMENT_NUMBER;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import br.com.zonework.coopvotes.structure.exception.BusinessException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidatorLegalDocumentNumberAssociate {

    private static final List<String> BLACK_LIST_NUMBERS = List.of(
        "11111111111",
        "22222222222",
        "33333333333",
        "44444444444",
        "55555555555",
        "66666666666",
        "77777777777",
        "88888888888",
        "99999999999",
        "99999999999"
    );

    public static void validate(String value) {
        value = value
            .replace(".", "")
            .replace("-", "");
        var expectedValue = value;
        if (BLACK_LIST_NUMBERS.stream().anyMatch(number -> number.equals(expectedValue))) {
            throw new BusinessException(BAD_REQUEST, LEGAL_DOCUMENT_NUMBER.getCode());
        }

        var cpfWithoutDigito = value.substring(0, value.length() - 2);
        var expectDigit = value.substring(value.length() - 2);

        var calculator = new CalculatorLegalDocumentNumber(cpfWithoutDigito)
            .withMultiplyFromTo(2, 11)
            .trocandoPorSeEncontrar("0", 10, 11)
            .mod(11);

        var digitOne = calculator.exec();
        var digitTwo = calculator.addDigit(digitOne).exec();

        if (Boolean.FALSE.equals(expectDigit.equals("%s%s".formatted(digitOne, digitTwo)))) {
            throw new BusinessException(BAD_REQUEST, LEGAL_DOCUMENT_NUMBER.getCode());
        }
    }

    private static class CalculatorLegalDocumentNumber {

        private final Map<Integer, String> replaces;
        private final LinkedList<Integer> numbers;
        private final List<Integer> multiplyers = new ArrayList<>();
        private int module;

        CalculatorLegalDocumentNumber(String cpf) {
            withMultiplyFromTo(2, 9);
            mod(11);
            replaces = new HashMap<>();
            numbers = new LinkedList<>();
            cpf.chars()
                .map(Character::getNumericValue)
                .forEach(numbers::add);

            Collections.reverse(numbers);
        }

        public CalculatorLegalDocumentNumber withMultiplyFromTo(int start, int end) {
            multiplyers.clear();
            IntStream.rangeClosed(start, end).forEach(multiplyers::add);
            return this;
        }

        public CalculatorLegalDocumentNumber trocandoPorSeEncontrar(String replace,
            Integer... keys) {
            Stream.of(keys).forEach(key -> replaces.put(key, replace));
            return this;
        }

        public CalculatorLegalDocumentNumber mod(int modulo) {
            this.module = modulo;
            return this;
        }

        public String exec() {
            var sum = 0;
            var turnMultiplier = 0;

            for (int digit : numbers) {
                var multiplicador = multiplyers.get(turnMultiplier);
                var total = digit * multiplicador;
                sum += total;
                turnMultiplier = nextMultiplier(turnMultiplier);
            }

            var result = sum % module;
            result = module - result;

            if (replaces.containsKey(result)) {
                return replaces.get(result);
            }

            return String.valueOf(result);
        }

        private int nextMultiplier(int multiplierNext) {
            multiplierNext++;
            if (multiplierNext == multiplyers.size()) {
                multiplierNext = 0;
            }
            return multiplierNext;
        }

        public CalculatorLegalDocumentNumber addDigit(String digito) {
            this.numbers.addFirst(Integer.valueOf(digito));
            return this;
        }
    }
}
