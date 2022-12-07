/*
 * Copyright (c) 2022 Broadcom.
 * The term "Broadcom" refers to Broadcom Inc. and/or its subsidiaries.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Broadcom, Inc. - initial API and implementation
 *
 */
package org.eclipse.lsp.cobol.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.lsp.cobol.common.AnalysisConfig;
import org.eclipse.lsp.cobol.common.copybook.CopybookProcessingMode;
import org.eclipse.lsp.cobol.common.copybook.SQLBackend;
import org.eclipse.lsp.cobol.common.EmbeddedLanguage;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.util.stream.Collectors.toList;
import static org.eclipse.lsp.cobol.service.utils.SettingsParametersEnum.*;

/** This service fetches configuration settings from the client. */
@Slf4j
@Singleton
public class CachingConfigurationService implements ConfigurationService {
  private final SettingsService settingsService;
  private CompletableFuture<ConfigurationEntity> config;

  @Inject
  public CachingConfigurationService(SettingsService settingsService) {
    this.settingsService = settingsService;
    config = CompletableFuture.completedFuture(new ConfigurationEntity());
  }

  @Override
  public void updateConfigurationFromSettings() {
    config =
        settingsService
            .fetchConfigurations(
                Arrays.asList(
                    TARGET_SQL_BACKEND.label,
                    ANALYSIS_FEATURES.label,
                    DIALECTS.label,
                    DACO_PREDEFINED_SECTIONS.label,
                    SUBROUTINE_LOCAL_PATHS.label,
                    CICS_TRANSLATOR_ENABLED.label))
            .thenApply(this::parseConfig);
  }

  @Override
  @SuppressWarnings("java:S2142")
  public AnalysisConfig getConfig(CopybookProcessingMode mode) {
    try {
      return AnalysisConfigHelper.fromConfigEntity(mode, config.get());
    } catch (InterruptedException e) {
      LOG.error("Issue while resolving analysis configuration", e);
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      LOG.error("Issue while resolving analysis configuration", e);
    }
    return AnalysisConfig.defaultConfig(mode);
  }

  @Override
  public List<String> getSubroutineDirectories() {
    try {
      return config.get().getSubroutines();
    } catch (InterruptedException e) {
      LOG.error("Issue while resolving subroutine configuration", e);
      Thread.currentThread().interrupt();
    } catch (ExecutionException e) {
      LOG.error("Issue while resolving subroutine configuration", e);
    }
    return ImmutableList.of();
  }

  private ConfigurationEntity parseConfig(List<Object> clientConfig) {
    return Optional.ofNullable(clientConfig)
        .map(this::parseSettings)
        .orElseGet(ConfigurationEntity::new);
  }

  private ConfigurationEntity parseSettings(List<Object> clientConfig) {

    return new ConfigurationEntity(
        parseSQLBackend(clientConfig.subList(0, 1)),
        parseFeatures((JsonElement) clientConfig.get(1)),
        parseDialects((JsonArray) clientConfig.get(2)),
        parsePredefinedParagraphs((JsonElement) clientConfig.get(3)),
        parseSubroutineFolder((JsonElement) clientConfig.get(4)),
        parseCicsTranslatorOption((JsonElement) clientConfig.get(5)));
  }

  private SQLBackend parseSQLBackend(List<Object> objects) {
    return SettingsService.getValueAsString(objects)
        .map(SQLBackend::valueOf)
        .orElse(SQLBackend.DB2_SERVER);
  }

  private boolean parseCicsTranslatorOption(JsonElement options) {
    if (options instanceof JsonNull) {
      return false;
    } else {
      return options.getAsBoolean();
    }
  }

  private List<String> parseDialects(JsonArray dialects) {
    List<String> providedDialects =
        Streams.stream(dialects).map(JsonElement::getAsString).collect(toList());
    return providedDialects;
  }

  private List<EmbeddedLanguage> parseFeatures(JsonElement features) {
    if (features.isJsonArray() && features.getAsJsonArray().size() > 0) {
      return Streams.stream((JsonArray) features)
          .map(JsonElement::getAsString)
          .map(EmbeddedLanguage::valueOf)
          .collect(toList());
    }
    return Arrays.asList(EmbeddedLanguage.values());
  }

  private List<String> parsePredefinedParagraphs(JsonElement labels) {
    if (labels.isJsonArray()) {
      return Streams.stream((JsonArray) labels).map(JsonElement::getAsString).collect(toList());
    }
    return ImmutableList.of();
  }

  private List<String> parseSubroutineFolder(JsonElement subroutine) {
    if (subroutine.isJsonArray()) {
      return Streams.stream((JsonArray) subroutine).map(JsonElement::getAsString).collect(toList());
    }
    return ImmutableList.of();
  }
}
