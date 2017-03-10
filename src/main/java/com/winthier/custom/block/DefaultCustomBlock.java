package com.winthier.custom.block;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor
public class DefaultCustomBlock implements CustomBlock {
    private final String customId;

    // TODO cancel *everything*
}
