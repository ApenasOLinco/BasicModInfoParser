package me.andreasmelone.basicmodinfoparser.platform.dependency.version;

import me.andreasmelone.basicmodinfoparser.platform.dependency.fabric.LooseSemanticVersion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LooseSemanticVersionFactory implements VersionFactory<LooseSemanticVersion> {
    private static final Pattern ALPHANUMERIC = Pattern.compile("[a-zA-Z0-9_\\-.+*]+");
    private static final Pattern VERSION_PATTERN = Pattern.compile("^([0-9xX*]+(?:\\.[0-9xX*]+)*)(-.*?)?(\\+.+)?$", Pattern.MULTILINE);

    @Override
    public Optional<LooseSemanticVersion> parseVersion(String versionString) {
        return parseVersion(versionString, false);
    }

    public Optional<LooseSemanticVersion> parseVersion(String versionString, boolean wildcards) {
        if (versionString == null || versionString.isEmpty() || !ALPHANUMERIC.matcher(versionString).matches())
            return Optional.empty();

        Matcher matcher = VERSION_PATTERN.matcher(versionString);
        if (!matcher.matches()) return Optional.empty();

        String prerelease = matcher.group(2);
        if (prerelease != null && !prerelease.isEmpty()) {
            prerelease = prerelease.substring(1);
        }

        String metadata = matcher.group(3);
        if (metadata != null && !metadata.isEmpty()) {
            metadata = metadata.substring(1);
        }

        String numbers = matcher.group(1);
        String[] splitNumbers = numbers.split("\\.");
        int[] versionInts = new int[splitNumbers.length];
        List<Integer> wildcardPositions = new ArrayList<>();

        for (int i = 0; i < splitNumbers.length; i++) {
            String num = splitNumbers[i];
            if (num.equalsIgnoreCase("x") || num.equals("*")) {
                if (!wildcards) return Optional.empty();
                versionInts[i] = 0;
                wildcardPositions.add(i);
                continue;
            }
            try {
                versionInts[i] = Integer.parseUnsignedInt(num);
            } catch (NumberFormatException ignored) {
                return Optional.empty();
            }
        }

        Integer prereleaseNumber = null;
        String[] prereleaseSplit = null;
        if (prerelease != null) {
            prereleaseSplit = prerelease.split("\\.", 2);
            if (prereleaseSplit.length > 1) {
                try {
                    prereleaseNumber = Integer.parseInt(prereleaseSplit[1]);
                    prerelease = prereleaseSplit[0];
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return Optional.of(
                new LooseSemanticVersion(
                        versionString,
                        versionInts,
                        wildcardPositions,
                        prerelease,
                        prereleaseNumber,
                        metadata,
                        wildcards
                )
        );
    }
}
