/*
 * Copyright (C) 2013-2020 52°North Initiative for Geospatial Open Source
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
package org.n52.io.quantity.generalize;

import org.n52.io.request.IoParameters;
import org.n52.io.request.RequestParameterSet;
import org.n52.io.response.dataset.DataCollection;
import org.n52.io.response.dataset.quantity.QuantityData;
import org.n52.series.spi.srv.DataService;
import org.n52.series.spi.srv.RawDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Composes a {@link DataService} instance to generalize requested timeseries data.
 */
public class GeneralizingQuantityService implements DataService<QuantityData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            GeneralizingQuantityService.class);

    private final DataService<QuantityData> composedService;

    public GeneralizingQuantityService(DataService<QuantityData> toCompose) {
        this.composedService = toCompose;
    }

    @Override
    public DataCollection<QuantityData> getData(RequestParameterSet parameters) {
        DataCollection<QuantityData> data = composedService.getData(parameters);
        DataCollection<QuantityData> ungeneralizedData = data;
        try {
            Generalizer<QuantityData> generalizer = GeneralizerFactory
                    .createGeneralizer(IoParameters.createFromQuery(parameters));
            DataCollection<QuantityData> generalizedData = generalizer
                    .generalize(ungeneralizedData);
            if (LOGGER.isDebugEnabled()) {
                logGeneralizationAmount(ungeneralizedData, generalizedData);
            }
            return generalizedData;
        } catch (GeneralizerException e) {
            LOGGER.error("Couldn't generalize timeseries collection. Returning original data.", e);
            return ungeneralizedData;
        }
    }

    private void logGeneralizationAmount(
            DataCollection<QuantityData> ungeneralizedData,
            DataCollection<QuantityData> generalizedData) {
        for (String timeseriesId : ungeneralizedData.getAllSeries().keySet()) {
            QuantityData originalTimeseries = ungeneralizedData.getSeries(timeseriesId);
            QuantityData generalizedTimeseries = generalizedData.getSeries(timeseriesId);
            int originalAmount = originalTimeseries.getValues().size();
            int generalizedAmount = generalizedTimeseries.getValues().size();
            LOGGER.debug("Generalized timeseries: {} (#{} --> #{}).",
                    timeseriesId, originalAmount, generalizedAmount);
        }
    }

    @Override
    public boolean supportsRawData() {
        return false;
    }

    @Override
    public RawDataService getRawDataService() {
        return null;
    }

}
