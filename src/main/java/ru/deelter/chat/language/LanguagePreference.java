package ru.deelter.chat.language;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguagePreference {

    private UUID playerId;
    private String speakerLanguage;
    private List<String> knownLanguages;
    private String primaryLanguage;
    private boolean toggleMode;
}