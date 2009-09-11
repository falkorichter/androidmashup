    <td><?php echo link_to($intent_handles_extra->getIdIntentHandlesExtra() ? $intent_handles_extra->getIdIntentHandlesExtra() : __('-'), 'IntentHandlesExtra/edit?id_intent_handles_extra='.$intent_handles_extra->getIdIntentHandlesExtra()) ?></td>
    <td><?php echo $intent_handles_extra->getIntentId() ?></td>
      <td><?php echo $intent_handles_extra->getExtraId() ?></td>
      <td><?php echo $intent_handles_extra->getMandatory() ?></td>
      <td><?php echo $intent_handles_extra->getDescription() ?></td>
  