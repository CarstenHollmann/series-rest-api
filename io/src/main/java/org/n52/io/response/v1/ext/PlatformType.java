/*
 * Copyright (C) 2013-2016 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public License
 * version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */
package org.n52.io.response.v1.ext;

/**
 * TODO: JavaDoc
 *
 * @author <a href="mailto:h.bredel@52north.org">Henning Bredel</a>
 */
public enum PlatformType {

    STATIONARY_INSITU("stationary/insitu"),
    STATIONARY_REMOTE("stationary/remote"),
    MOBILE_INSITU("mobile/insitu"),
    MOBILE_REMOTE("mobile/remote");

    private final String typeName;

    private PlatformType(String typeName) {
        this.typeName = typeName;
    }

    public String getIdPrefix() {
        return this.name().toLowerCase();
    }

    public String getPlatformType() {
        return typeName;
    }

    public static String extractId(String id) {
        if (isStationaryId(id)) {
            if (isInsitu(id)) {
                return id.substring(STATIONARY_INSITU.getIdPrefix().length() + 1);
            } else {
                return id.substring(STATIONARY_REMOTE.getIdPrefix().length() + 1);
            }
        } else if (isMobileId(id)) {
            if (isInsitu(id)) {
                return id.substring(MOBILE_INSITU.getIdPrefix().length() + 1);
            } else {
                return id.substring(MOBILE_REMOTE.getIdPrefix().length() + 1);
            }
        }
        return id;
    }

    public static boolean isStationaryId(String id) {
        return id.startsWith("stationary");
    }

    public static boolean isMobileId(String id) {
        return id.startsWith("mobile");
    }

    public static boolean isRemoteId(String id) {
        return id.endsWith("remote");
    }

    public static boolean isInsitu(String id) {
        return id.endsWith("insitu");
    }

    public static boolean isKnownType(String typeName) {
        for (PlatformType type : values()) {
            if (type.getPlatformType().equalsIgnoreCase(typeName)) {
                return true;
            }
        }
        return false;
    }

    public static PlatformType toInstance(String typeName) {
        for (PlatformType type : values()) {
            if (type.getPlatformType().equalsIgnoreCase(typeName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("no type for '" + typeName + "'.");
    }

    public static PlatformType toInstance(boolean mobile, boolean insitu) {
        if (mobile) {
            return insitu
                    ? MOBILE_INSITU
                    : MOBILE_REMOTE;
        } else {
            return insitu
                    ? STATIONARY_INSITU
                    : STATIONARY_REMOTE;
        }
    }
}
