package com.endava.internship.service;

import com.endava.internship.domain.Privilege;
import com.endava.internship.domain.User;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserServiceImpl implements UserService {

    @Override
    public List<String> getFirstNamesReverseSorted(List<User> users) {
        return users.stream()
                .map(User::getFirstName)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    @Override
    public List<User> sortByAgeDescAndNameAsc(final List<User> users) {
        return users.stream()
                .sorted(Comparator.comparing(User::getFirstName))
                .sorted(Comparator.comparing(User::getAge).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Privilege> getAllDistinctPrivileges(final List<User> users) {
        return users.stream()
                .flatMap(user -> user.getPrivileges().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUpdateUserWithAgeHigherThan(final List<User> users, final int age) {
        return users.stream()
                .filter(user -> user.getAge() > age && user.getPrivileges().contains(Privilege.UPDATE))
                .findFirst();
    }

    @Override
    public Map<Integer, List<User>> groupByCountOfPrivileges(final List<User> users) {
        return users.stream()
                .collect(Collectors.groupingBy(user -> user.getPrivileges().size()));
    }

    @Override
    public double getAverageAgeForUsers(final List<User> users) {
        Double avg = users.stream()
                .collect(Collectors.averagingDouble(User::getAge));
        return avg == 0 ? -1 : avg;
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
        Map<String, Integer> lastNamesEntrances = users.stream()
                .collect(Collectors.toMap(user -> user.getLastName().toLowerCase(), user -> 1, Integer::sum));

        Map.Entry<String, Integer> resultEntry = lastNamesEntrances.entrySet().stream()
                .max(Map.Entry.comparingByValue()).orElse(new AbstractMap.SimpleEntry<>("", 0));

        return resultEntry.getValue() > 1 ? Optional.ofNullable(resultEntry.getKey()) : Optional.empty();
    }

    @Override
    public List<User> filterBy(final List<User> users, final Predicate<User>... predicates) {
        return users.stream()
                .filter(Stream.of(predicates).reduce(x -> true, Predicate::and))
                .collect(Collectors.toList());
    }

    @Override
    public String convertTo(final List<User> users, final String delimiter, final Function<User, String> mapFun) {
        return users.stream()
                .map(User::getLastName)
                .reduce((str1, str2) -> String.join(delimiter, str1, str2))
                .orElse("");
    }

    @Override
    public Map<Privilege, List<User>> groupByPrivileges(List<User> users) {
        return Stream.of(Privilege.values())
                .collect(Collectors.toMap(privilege -> privilege, privilege -> users.stream()
                        .filter(user -> user.getPrivileges().contains(privilege))
                        .collect(Collectors.toList())));
    }

    @Override
    public Map<String, Long> getNumberOfLastNames(final List<User> users) {
        return users.stream()
                .collect(Collectors.toMap(User::getLastName, user -> 1L, Long::sum));
    }
}
