package me.andreasmelone.basicmodinfoparser.platform.dependency.version;

import me.andreasmelone.basicmodinfoparser.platform.dependency.forge.MavenVersion;
import me.andreasmelone.basicmodinfoparser.platform.dependency.forge.MavenVersion.VersionSegment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenVersionFactory implements VersionFactory<MavenVersion> {
    private static final Pattern ALPHANUMERIC = Pattern.compile("[a-zA-Z0-9_\\-.]+");
    private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d*)?(\\D+)(\\d*)?$");

    @Override
    public Optional<MavenVersion> parseVersion(String versionString) {
        if (versionString == null || versionString.isEmpty() || !ALPHANUMERIC.matcher(versionString).matches())
            return Optional.empty();

        List<VersionSegment> segments = new ArrayList<>();

        String noHyphens = versionString.replace("-", ".");
        String[] splitByDot = noHyphens.split("\\.");
        for (String segment : splitByDot) {
            Matcher matcher = VERSION_PATTERN.matcher(segment);
            if (!matcher.matches()) {
                try {
                    segments.add(new VersionSegment.NumberVersionSegment(Integer.parseUnsignedInt(segment)));
                } catch (NumberFormatException ignored) {
                    //
                }

                continue;
            }

            String firstNumber = matcher.group(1);
            String string = matcher.group(2);
            String secondNumber = matcher.group(3);

            if (firstNumber != null && !firstNumber.isEmpty()) {
                try {
                    segments.add(new VersionSegment.NumberVersionSegment(Integer.parseUnsignedInt(firstNumber)));
                } catch (NumberFormatException ignored) {
                }
            }
            if (string != null && !string.isEmpty()) {
                VersionSegment.QualifierVersionSegment.Qualifier qualifier = VersionSegment.QualifierVersionSegment.Qualifier.getByName(string);
                if (qualifier == null) {
                    segments.add(new VersionSegment.StringVersionSegment(string));
                } else {
                    segments.add(new VersionSegment.QualifierVersionSegment(qualifier));
                }
            }
            if (secondNumber != null && !secondNumber.isEmpty()) {
                try {
                    segments.add(new VersionSegment.NumberVersionSegment(Integer.parseUnsignedInt(secondNumber)));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return Optional.of(
                new MavenVersion(
                        versionString,
                        segments.toArray(new VersionSegment[0])
                )
        );
    }
}
