package com.redsponge.dbf.intro;

public enum IntroFrame {
    LONG_AGO("Long ago, There was a Global Thermonuclear War"),
    EVERYTHING_DEAD("Almost all life, including all of humanity, was wiped out."),
    THOUSANDS_OF_YEARS_LATER("For thousands of years, the earth didn't have any harming life on it, which allowed it to regrow.. mostly"),
    MUTANTS("Radiation and nuclear waste have caused most animals to mutate..."),
    DASHNI("Some became really cool rabbit-bunny-humanoid creatures"),
    OTHERS("While some.. weren't as lucky..."),
    EXCLAMATION_CARROT(""),
    OH_LO_MA_NAASE("")
    ;

    public static final IntroFrame[] ALL = {LONG_AGO, EVERYTHING_DEAD, THOUSANDS_OF_YEARS_LATER, MUTANTS, DASHNI, OTHERS, EXCLAMATION_CARROT, OH_LO_MA_NAASE};

    private String text;

    IntroFrame(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}