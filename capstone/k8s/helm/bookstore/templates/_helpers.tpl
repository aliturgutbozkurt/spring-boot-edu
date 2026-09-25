{{/* Labels shared by all objects of this release */}}
{{- define "bookstore.labels" -}}
app.kubernetes.io/part-of: bookstore
app.kubernetes.io/managed-by: {{ .Release.Service }}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
{{- end }}

{{/*
A value from the Secret "bookstore": the given value, the value already stored in the cluster (upgrade),
or a new random one (first install).
*/}}
{{- define "bookstore.secretValue" -}}
{{- $given := index . 0 -}}
{{- $key := index . 1 -}}
{{- $root := index . 2 -}}
{{- if $given -}}
{{- $given | b64enc -}}
{{- else -}}
{{- $existing := lookup "v1" "Secret" $root.Release.Namespace "bookstore" -}}
{{- if and $existing (hasKey $existing.data $key) -}}
{{- index $existing.data $key -}}
{{- else -}}
{{- randAlphaNum 48 | b64enc -}}
{{- end -}}
{{- end -}}
{{- end }}
