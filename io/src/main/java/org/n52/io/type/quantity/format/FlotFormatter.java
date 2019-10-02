/*
 * Copyright (C) 2013-2019 52°North Initiative for Geospatial Open Source
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
package org.n52.io.type.quantity.format;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.n52.io.format.DataFormatter;
import org.n52.io.response.dataset.Data;
import org.n52.io.response.dataset.DataCollection;
import org.n52.io.response.dataset.DatasetMetadata;
import org.n52.io.response.dataset.quantity.QuantityValue;

public class FlotFormatter implements DataFormatter<Data<QuantityValue>, FlotData> {

    @Override
    public FlotDataCollection format(DataCollection<Data<QuantityValue>> toFormat) {
        FlotDataCollection flotDataCollection = new FlotDataCollection();
        for (String timeseriesId : toFormat.getAllSeries().keySet()) {
            Data<QuantityValue> seriesToFormat = toFormat.getSeries(timeseriesId);
            FlotData series = createFlotSeries(seriesToFormat);
            flotDataCollection.addNewSeries(timeseriesId, series);
        }
        return flotDataCollection;
    }

    private FlotData createFlotSeries(Data<QuantityValue> seriesToFormat) {
        FlotData flotSeries = new FlotData();
        flotSeries.setValues(formatSeries(seriesToFormat));
        if (seriesToFormat.hasMetadata()) {
            formatMetadata(seriesToFormat, flotSeries);
        }
        return flotSeries;
    }

    private void formatMetadata(Data<QuantityValue> seriesToFormat, FlotData flotSeries) {
        DatasetMetadata<QuantityValue> metadata = seriesToFormat.getMetadata();
        Map<String, Data<QuantityValue>> referenceValues = metadata.getReferenceValues();
        for (String referenceValueId : referenceValues.keySet()) {
            Data<QuantityValue> referenceValueData = metadata.getReferenceValues().get(referenceValueId);
            flotSeries.addReferenceValues(referenceValueId, formatSeries(referenceValueData));
        }
        flotSeries.setValueBeforeTimespan(formatValue(metadata.getValueBeforeTimespan()));
        flotSeries.setValueAfterTimespan(formatValue(metadata.getValueAfterTimespan()));
    }

    private List<Number[]> formatSeries(Data<QuantityValue> referenceValueData) {
        List<Number[]> series = new ArrayList<>();
        for (QuantityValue currentValue : referenceValueData.getValues()) {
            series.add(formatValue(currentValue));
        }
        return series;
    }

    private Number[] formatValue(QuantityValue currentValue) {
        if (currentValue == null) {
            return null;
        }
        BigDecimal value = currentValue.getValue();
        Long timestamp = currentValue.getTimestamp().getMillis();
        return new Number[] { timestamp, value };
    }

}